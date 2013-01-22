#!/bin/env python2.7

import sys
import glob
import os
import json
import gflags
import feature_extractor

FLAGS = gflags.FLAGS

gflags.DEFINE_string("input_dir", None, "Directory with input text files")
gflags.MarkFlagAsRequired("input_dir")

gflags.DEFINE_string("features_filename", None,
    "Output file with features in creg format")
gflags.MarkFlagAsRequired("features_filename")

gflags.DEFINE_string("labels_filename", None,
    "Output file with labels in creg format")
gflags.MarkFlagAsRequired("labels_filename")


class InstanceExtractor:
  def __init__(self, feature_extractors, features_file, labels_file):
    self.feature_extractors = feature_extractors
    self.features_file = features_file
    self.labels_file = labels_file

  def ProcessInputDir(self, input_dir):
   """Traverses all subdirectories, assuming their names are 3-letters."""
   for dirname in sorted(glob.iglob(os.path.join(input_dir, "???"))):
     self._ProcessLanguageDir(dirname)

  def _ProcessLanguageDir(self, dirname):
    """Processing all .txt files in a directory."""
    language = os.path.basename(dirname)
    for filename in sorted(glob.iglob(os.path.join(dirname, "*.txt"))):
      self._ProcessSingleFile(filename, language)

  def _ProcessSingleFile(self, filename, language):
    """Extracts features from an input file using feature_extractors."""
    base_filename = os.path.basename(filename)
    instance = "_".join( (language, base_filename) )
    text = open(filename).read()
    feature_dict = {}
    for extractor in self.feature_extractors:
      feature_dict.update(
          extractor.ExtractFeaturesFromInstance(text, language, filename))  
    self._WriteFeatures(instance, feature_dict)
    self._WriteLabel(instance, language)

  def _WriteFeatures(self, instance, feature_dict):
    """Saves features in Creg format."""
    features_str = json.dumps(feature_dict, sort_keys=True)
    self.features_file.write("{0}\t{1}\n".format(instance, features_str))

  def _WriteLabel(self, instance, label):
    """Saves label (responses) file in Creg format."""
    self.labels_file.write("{0}\t{1}\n".format(instance, label))


def main(argv):
  try:
    argv = FLAGS(argv)  # parse flags
  except gflags.FlagsError, e:
    print '%s\nUsage: %s\n%s' % (e, sys.argv[0], FLAGS)
    sys.exit(1)
  feature_extractors = feature_extractor.Repository.GetActiveFeatureExtractors()
  instance_extractor = InstanceExtractor(feature_extractors,
      open(FLAGS.features_filename, "w"), open(FLAGS.labels_filename, "w"))
  instance_extractor.ProcessInputDir(FLAGS.input_dir)


if __name__ == '__main__':
  main(sys.argv)
