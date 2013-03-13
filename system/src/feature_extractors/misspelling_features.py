#!/usr/bin/python

import collections
import math
import sys
import gflags
import feature_extractor
import os 

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("misspelling_features", False,
    "Use misspelled words as a source of features")

gflags.DEFINE_string("corrected_document_path", "../corrected",
   "Path to corrected documents")

class MisspellingsFeatureExtractor(feature_extractor.FeatureExtractor):
  def __init__(self, cdpath):
    self.cdpath = cdpath

  def ExtractFeaturesFromInstance(self, text, language, filename):
    result = {}
    sys.stderr.write("Filename: %s\n" % filename)
    return result

if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.misspelling_features:
    return None
  return MisspellingsFeatureExtractor(FLAGS.corrected_document_path)

