#!/usr/bin/python
""" Translationese feature:
This hypothesis checks whether pronouns from
:mod:`translationese.function_words` alone can yield a high classification
accuracy. Each pronoun in the corpus is a feature, whose value is the
normalized frequency of its occurrences in the chunk.
"""

import collections
import math
import sys
import gflags
import feature_extractor

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_document_length", True,
    "Append the length of a document")


"""Counts all words in a document
"""
class DocumentLengthFeatureExtractor(feature_extractor.FeatureExtractor):
  def ExtractFeaturesFromInstance(self, text, language, filename):
    return math.log(len(text.split())+1)
   
if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_document_length:
    return None
  return DocumentLengthFeatureExtractor()

