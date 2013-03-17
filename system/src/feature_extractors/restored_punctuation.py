#!/usr/bin/python
""" Counts of restored punctuation marks 
(only prepositions and definite/indefinite articles) 
1) train LM on corrected training data 
2) create a file with function words 
3) run hidden-ngram on the training data without function words
4) our features - ratio of matches/insertions/deletions for each function word.
   matches/insertions/deletions computed using a rough estimation: comparison 
   of the number of a function word in each line of corrected and predicted texts
"""

import collections
import math
import sys
import gflags
import feature_extractor
import itertools
import os
import re 

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_restored_punctuation_features", False,
    "Append counts of restored punctuation marks.")

FUNCTION_WORDS = [",", "'", "/", '"', "?", "!", ".", ":", ";", "-","(", ")", "[", "]"]

class RestoredPunctuationFeatureExtractor(feature_extractor.FeatureExtractor):
  def ExtractFeaturesFromInstance(self, text, prompt, language, filename):
    counts = collections.defaultdict(int)

    insertions = collections.defaultdict(int)
    deletions = collections.defaultdict(int)
    matches = collections.defaultdict(int)

    corrected_filename = re.sub(r'/tokenized/', r'/corrected/', filename)
    predicted_filename = re.sub(r'/tokenized/', r'/restored_punctuation/', filename)

    for correct, predicted in itertools.izip(open(corrected_filename), open(predicted_filename)):
      correct_tokens = correct.split()
      predicted_tokens = predicted.split()
      in_correct = collections.defaultdict(int)
      in_predicted = collections.defaultdict(int)  
      for word in FUNCTION_WORDS:
        for w in correct_tokens:
          if w == word:
            in_correct[word]+=1 
        for w in predicted_tokens:
          if w == word:
            in_predicted[word]+=1       
      # update total counts. not edit distance, just rough estimation
      # check numbers of matches/insertions/deletions per line
      for word in FUNCTION_WORDS:
        in_c = in_correct[word]
        in_p = in_predicted[word]
        if in_c == 0 and in_p == 0:
          continue
        if in_c < in_p:
          matches[word] += in_c
          insertions[word] += in_p - in_c
        else: 
          matches[word] += in_p
          deletions[word] += in_c - in_p
      
    # normalize for each function word
    for word in FUNCTION_WORDS:
      total =  float(matches[word] + insertions[word] + deletions[word])
      if total > 0:
        if matches[word] > 0:
          counts["Match_" + word] = math.log(matches[word]) #/total  
          counts["Match_p_" + word] = matches[word]/total

        if insertions[word] > 0:
          counts["Insert_" + word] = math.log(insertions[word]) #/total
          counts["Insert_p_" + word] = insertions[word]/total

        if deletions[word] > 0:
          counts["Delete_" + word] = math.log(deletions[word]) #/total
          counts["Delete_p_" + word] = deletions[word]/total

    return counts

if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_restored_punctuation_features:
    return None
  return RestoredPunctuationFeatureExtractor()

