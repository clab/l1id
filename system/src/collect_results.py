#!/bin/env python2.7
from __future__ import division
import sys
import glob
import os
import re
import gflags
from collections import Counter

FLAGS = gflags.FLAGS

gflags.DEFINE_string("creg_results_file_pattern", None,
    "File pattern of the creg STDERR output")

gflags.DEFINE_string("creg_predictions_file_pattern", None,
    "File pattern of the creg STDOUT output")

gflags.DEFINE_string("creg_results_files", None,
    "List of creg STDERR output files")

gflags.DEFINE_string("creg_predictions_files", None,
    "List of creg STDOUT output files")


gflags.DEFINE_string("metadata_file", None,
    "CSV file with metadata about the corpora")
gflags.MarkFlagAsRequired("metadata_file")

gflags.DEFINE_string("out_file", None,
    "Filename with results summary. Defaults to STDOUT")

def avg(l):
  return sum(l)/len(l)

def PRF(tp, fp, fn):
  assert tp>=0 and fp>=0 and fn>=0
  p = tp/(tp+fp) if (tp+fp>0) else float('NaN')
  r = tp/(tp+fn) if (tp+fn>0) else float('NaN')
  f = 2*p*r/(p+r) if p>0 and r>0 else float('NaN')
  return p, r, f

def ReadAccuracy(filename):
  line = ""
  try:
    # get last line
    line = open(filename).readlines()[-1]
  except:
    pass  # File is empty
  match = re.match("Held-out accuracy: ([0-9.]+)", line)
  if match is None:
    print "No accuracy in file:", filename
    return None
  else:
    return float(match.group(1))

def CollectAccuracy(creg_results_file_pattern, results_files, out_file):
  experiment_stats = []
  for filename in results_files or glob.iglob(creg_results_file_pattern):
    accuracy = ReadAccuracy(filename)
    if accuracy is not None:
      experiment_stats.append(accuracy)
  if len(experiment_stats) > 0:
    out_file.write(
        "Per-fold accuracy:\n\tAverage: {0}\n\tRange: [{1}..{2}]\n".format(
        avg(experiment_stats), min(experiment_stats), max(experiment_stats)))

def ReadCorpusMetadata(metadata_filename):
  result = {}
  for line in open(metadata_filename):
    filename, prompt, language, level = line.strip().split(",")
    result[(language, filename)] = (prompt, level)
  return result

def ReadPredictions(filename):
  result = {}
  for line in open(filename):
    instance, predicted_label = line.strip().split("\t")[:2]
    sep_pos = instance.find("_")
    lang = instance[:sep_pos]
    test_filename = instance[sep_pos+1:]
    result[(lang, test_filename)] = predicted_label
  return result

def CollectPredictions(creg_predictions_file_pattern, predictions_files):
  result = {}
  nFiles = 0
  for filename in predictions_files or glob.iglob(creg_predictions_file_pattern):
    result.update(ReadPredictions(filename))
    nFiles += 1
  return nFiles, result

def PrintAccMatrix(title, matrix, out_file):
  out_file.write("\n\nResults by {}:\n".format(title))
  out_file.write("\tCorrect\tWrong\tAcc\tRel Freq")
  properties = sorted({x for x,y in matrix})
  totals = Counter({x: matrix[(x,True)]+matrix[(x,False)] for x in properties})
  for prop in properties:
    out_file.write("\n{}".format(prop))
    for is_correct in [True, False]:
      out_file.write("\t{}".format(matrix[(prop, is_correct)]))
    total = totals[prop]
    accuracy = matrix[(prop, True)] / total
    freq = total / sum(totals.values())
    out_file.write("\t{:.3f}\t{:.3f}".format(accuracy, freq))
  out_file.write("\n")
  
def PrintPrecRecMatrix(title, predictions, out_file):
  out_file.write("\n\nResults by {}:\n".format(title))
  out_file.write("\tCorrect\tFP\tPrec\tFN\tRecall\tF1")
  tpC, fpC, fnC = Counter(), Counter(), Counter()
  n = 0
  for (gold, itemid), pred in predictions.iteritems():
    n += 1
    if pred==gold:
      tpC[gold] += 1
    else:
      fpC[pred] += 1
      fnC[gold] += 1
  for prop in sorted(set(tpC.keys()+fpC.keys()+fnC.keys())):
    out_file.write("\n{}".format(prop))
    prec, rec, f1 = PRF(tpC[prop], fpC[prop], fnC[prop])
    out_file.write("\t{}\t{}\t{:.3f}\t{}\t{:.3f}\t{:.3f}".format(tpC[prop],fpC[prop],prec,fnC[prop],rec,f1))
  out_file.write("\n")

def PrintConfusionMatrix(predictions, out_file):
  confusion_matrix = Counter()
  all_languages = set()
  for (lang, filename), predicted_label in predictions.iteritems():
    all_languages.add(lang)
    confusion_matrix[(predicted_label, lang)] += 1
  out_file.write("\n\nConfusion Matrix:\nCorrect\\Predicted\n")
  all_languages = sorted(all_languages)
  out_file.write("\t{}".format("\t".join(all_languages)))
  for lang in all_languages:
    out_file.write("\n{}".format(lang))
    for predicted_lang in all_languages:
      out_file.write("\t{}".format(confusion_matrix[(predicted_lang, lang)]))
  out_file.write("\n")

def PrintProfiledResults(predictions, metadata, out_file):
  level_matrix = Counter()
  prompt_matrix = Counter()
  level_dist = Counter()
  correct_count = 0
  for (lang, filename), predicted_label in predictions.iteritems():
    prompt, level = metadata.get( (lang, filename), ("unk", "unk") )
    level = level.replace('low','1 low').replace('medium', '2 med').replace('high', '3 high')
    level_dist[level] += 1
    is_correct = (lang == predicted_label)
    level_matrix[(level, is_correct)] += 1
    prompt_matrix[(prompt, is_correct)] += 1
    if is_correct:
      correct_count += 1
  PrintPrecRecMatrix("language", predictions, out_file)
  PrintAccMatrix("level", level_matrix, out_file)
  PrintAccMatrix("prompt", prompt_matrix, out_file)
  accuracy = correct_count/len(predictions)
  out_file.write("\n\nTotal accuracy:\t{}\n\n".format(accuracy))

def main(argv):
  try:
    argv = FLAGS(argv)  # parse flags
    assert (FLAGS.creg_predictions_file_pattern is not None) ^ (FLAGS.creg_predictions_files is not None)
    assert (FLAGS.creg_results_file_pattern is not None) ^ (FLAGS.creg_results_files is not None)
  except (gflags.FlagsError, AssertionError), e:
    print '%s\nUsage: %s\n%s' % (e, sys.argv[0], FLAGS)
    sys.exit(1)
  if FLAGS.out_file is not None:
    out_file = open(FLAGS.out_file, "w")
  else:
    out_file = sys.stdout

  corpus_metadata = ReadCorpusMetadata(FLAGS.metadata_file)
  nFiles, file_predictions = CollectPredictions(FLAGS.creg_predictions_file_pattern, FLAGS.creg_predictions_files.split() if FLAGS.creg_predictions_files else None)
  out_file.write('Results collected over %d files' % nFiles)
  PrintConfusionMatrix(file_predictions, out_file)
  PrintProfiledResults(file_predictions, corpus_metadata, out_file)
  CollectAccuracy(FLAGS.creg_results_file_pattern, FLAGS.creg_results_files.split() if FLAGS.creg_results_files else None, out_file)
 
if __name__ == '__main__':
  main(sys.argv)


