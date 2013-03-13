#!/usr/bin/python

import collections
import math
import sys
import gflags
import feature_extractor
import re
import os 
import subprocess

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("misspelling_features", False,
    "Use misspelled words as a source of features")

gflags.DEFINE_string("misspelling_extractor", "",
   "Path to auxilliary feature extractor")

class MisspellingsFeatureExtractor(feature_extractor.FeatureExtractor):
  def __init__(self, cdpath):
    self.cdpath = cdpath

  def ExtractFeaturesFromInstance(self, text, language, filename):
    result = {}
    # /home/cdyer/l1id/system/data/input/NLI_2013_Training_Data/tokenized/1054261.txt
    corrected = re.sub(r'/tokenized/', r'/corrected/', filename)
    for feat in subprocess.check_output([self.cdpath,filename,corrected]).rstrip().split('\n'):
      (f, val) = feat.split('\t')
      result[f] = float(val)
    return result

if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.misspelling_features:
    return None
  return MisspellingsFeatureExtractor(FLAGS.misspelling_extractor)

