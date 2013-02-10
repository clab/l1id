#!/bin/env python2.7

import sys
import glob
import os
import re
import gflags
import collections

FLAGS = gflags.FLAGS

gflags.DEFINE_string("creg_results_file_pattern", None,
    "File pattern of the creg STDERR output")
gflags.MarkFlagAsRequired("creg_results_file_pattern")

gflags.DEFINE_string("creg_predictions_file_pattern", None,
    "File pattern of the creg STDOUT output")
gflags.MarkFlagAsRequired("creg_predictions_file_pattern")

gflags.DEFINE_string("metadata_file", None,
    "CSV file with metadata about the corpora")
gflags.MarkFlagAsRequired("metadata_file")

gflags.DEFINE_string("out_file", None,
    "Filename with results summary. Defaults to STDOUT")

def avg(l):
  return sum(l)/len(l)

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

def CollectAccuracy(creg_results_file_pattern, out_file):
  experiment_stats = []
  for filename in glob.iglob(creg_results_file_pattern):
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
    instance, predicted_label, weights = line.strip().split("\t")
    lang, test_filename = instance.split("_")
    result[(lang, test_filename)] = predicted_label
  return result

def CollectPredictions(creg_predictions_file_pattern):
  result = {}
  for filename in glob.iglob(creg_predictions_file_pattern):
    result.update(ReadPredictions(filename))
  return result

def PrintResultsMatrix(title, matrix, out_file):
  out_file.write("\n\nResults by {}:\n".format(title))
  out_file.write("\tCorrect\tWrong\tAccuracy")
  properties = sorted(set([x for x,y in matrix]))
  for prop in properties:
    out_file.write("\n{}".format(prop))
    for is_correct in [True, False]:
      out_file.write("\t{}".format(matrix[(prop, is_correct)]))
    total = matrix[(prop, True)] + matrix[(prop, False)]
    accuracy = matrix[(prop, True)] / float(total)
    out_file.write("\t{:.3f}".format(accuracy))
  out_file.write("\n")

def PrintConfusionMatrix(predictions, out_file):
  confusion_matrix = collections.defaultdict(int)
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
  language_matrix = collections.defaultdict(int)
  level_matrix = collections.defaultdict(int)
  prompt_matrix = collections.defaultdict(int)
  correct_count = 0
  for (lang, filename), predicted_label in predictions.iteritems():
    prompt, level = metadata[(lang, filename)]
    is_correct = (lang == predicted_label)
    language_matrix[(lang, is_correct)] += 1
    level_matrix[(level, is_correct)] += 1
    prompt_matrix[(prompt, is_correct)] += 1
    if is_correct:
      correct_count += 1
  PrintResultsMatrix("language", language_matrix, out_file)
  PrintResultsMatrix("level", level_matrix, out_file)
  PrintResultsMatrix("prompt", prompt_matrix, out_file)
  accuracy = correct_count/float(len(predictions))
  out_file.write("\n\nTotal accuracy:\t{}\n\n".format(accuracy))

def main(argv):
  try:
    argv = FLAGS(argv)  # parse flags
  except gflags.FlagsError, e:
    print '%s\nUsage: %s\n%s' % (e, sys.argv[0], FLAGS)
    sys.exit(1)
  if FLAGS.out_file is not None:
    out_file = open(FLAGS.out_file, "w")
  else:
    out_file = sys.stdout

  corpus_metadata = ReadCorpusMetadata(FLAGS.metadata_file)
  file_predictions = CollectPredictions(FLAGS.creg_predictions_file_pattern)
  PrintConfusionMatrix(file_predictions, out_file)
  PrintProfiledResults(file_predictions, corpus_metadata, out_file)
  CollectAccuracy(FLAGS.creg_results_file_pattern, out_file)
 
if __name__ == '__main__':
  main(sys.argv)


