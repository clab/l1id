#!/bin/env python2.7
from __future__ import print_function
import sys
import glob
import os
import json
import gflags
import codecs
import feature_extractor

FLAGS = gflags.FLAGS

gflags.DEFINE_string("input_dir", None, "Directory with input text files")
gflags.MarkFlagAsRequired("input_dir")

gflags.DEFINE_string("metadata_filename", None, "Path to the index-training.csv")
gflags.MarkFlagAsRequired("metadata_filename")

gflags.DEFINE_string("features_filename", None,
    "Output file with features in creg format")
gflags.MarkFlagAsRequired("features_filename")

gflags.DEFINE_string("labels_filename", None,
    "Output file with labels in creg format")
gflags.MarkFlagAsRequired("labels_filename")


class InstanceExtractor:
  def __init__(self, feature_extractors, file_to_lang, 
               features_file, labels_file):
    self.feature_extractors = feature_extractors
    self.file_to_lang_dict = file_to_lang
    self.features_file = features_file
    self.labels_file = labels_file

  def ProcessInputDir(self, input_dir):
    """Processing all .txt files in a directory."""
    nProcessed = 0
    for filename in sorted(glob.iglob(os.path.join(input_dir, "*.txt"))):
      #lang = self.file_to_lang_dict[os.path.basename(filename)]
      #self._ProcessSingleFile(filename, lang)
      lang = self.file_to_lang_dict[os.path.basename(filename)][0]
      prompt = self.file_to_lang_dict[os.path.basename(filename)][1]
      self._ProcessSingleFile(filename,lang,prompt)
      nProcessed += 1
    assert nProcessed>0, 'No .txt files found in {}'.format(input_dir)

  #def _ProcessSingleFile(self, filename, language):
  def _ProcessSingleFile(self, filename, language,prompt):
    """Extracts features from an input file using feature_extractors."""
    base_filename = os.path.basename(filename)
    instance = "_".join( (language, base_filename) )
    #text = open(filename).read()
    text = codecs.open(filename,"r","utf-8").read()
    feature_dict = {}
    for extractor in self.feature_extractors:
      feature_dict.update(
          #extractor.ExtractFeaturesFromInstance(text, language, filename))
          extractor.ExtractFeaturesFromInstance(text, prompt, language, filename))
    self._WriteFeatures(instance, feature_dict)
    self._WriteLabel(instance, language)

  def _WriteFeatures(self, instance, feature_dict):
    """Saves features in Creg format."""
    features_str = json.dumps(feature_dict, sort_keys=True)
    self.features_file.write("{0}\t{1}\n".format(instance, features_str))

  def _WriteLabel(self, instance, label):
    """Saves label (responses) file in Creg format."""
    self.labels_file.write("{0}\t{1}\n".format(instance, label))


def LoadMetadata(metadata_file):
  file_to_lang = {}
  for line in metadata_file:
    # 278.txt,P6,GER,medium
    if line.count(",")==3:
      filename, prompt, lang, level = line.strip().split(",")
    else:
      filename, prompt, level = line.strip().split(",")  # language unavailable in test set
      lang = '???'
    #file_to_lang[filename] = lang
    file_to_lang[filename] = [lang,prompt]
  return file_to_lang

def main(argv):
  try:
    argv = FLAGS(argv)  # parse flags
  except gflags.FlagsError, e:
    print('%s\nUsage: %s\n%s' % (e, sys.argv[0], FLAGS), file=sys.stderr)
    sys.exit(1)
  file_to_lang = LoadMetadata(open(FLAGS.metadata_filename))
  feature_extractors = feature_extractor.Repository.GetActiveFeatureExtractors()
  print('Feature extractors:',feature_extractors, file=sys.stderr)
  instance_extractor = InstanceExtractor(feature_extractors, file_to_lang,
      open(FLAGS.features_filename, "w"), open(FLAGS.labels_filename, "w"))
  instance_extractor.ProcessInputDir(FLAGS.input_dir)


if __name__ == '__main__':
  main(sys.argv)
