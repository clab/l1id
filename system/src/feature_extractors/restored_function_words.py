#!/usr/bin/python
""" Counts of restored function words 
WH-words, prepositions, conjunctions, articles, auxiliary verbs, 
quantifiers, personal pronouns, possessive pronouns, quantified pronouns

1) train LM on corrected training data 
2) create a file with function words 
3) run hidden-ngram on the training data without function words
4) our features - matches/insertions/deletions for each function word.
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

gflags.DEFINE_bool("append_restored_function_words_features", False,
    "Append counts of restored function words.")

FUNCTION_WORDS =  ["of","in","for","to","with","on","if","as","than","by",
"because","about","from","at","like","without","after","so","while","since",
"into","during","through","over","before","between","although","around",
"though","whether","towards","behind","among","above","under","out",
"until","whereas","within","against","unless","besides","upon","throughout",
"once","per","along","despite","across","till","toward","except","beyond","up",
"outside","inside","near","unlike","beside","off","onto","amongst","beneath",
"versus","whilst","thoughout","albeit","such","regarding","notwithstanding",
"here","where","away","vs.","e.g.","i.e.","respecting","insofar","concerning",
"this","that","these","those","what","where","which","who","whose","when",
"why","and","but","or","either","neither","can","may","will","shall","could",
"might","would","should","must","do","does","doing","did","done","am","is",
"are","was","were","be","been","being","have","has","having","had","all",
"some","every","each","few","both","many","much","no","i","you","he","she",
"it","we","they","me","him","her","it","us","them","one","none","my","mine",
"your","yours","his","hers","its","our","ours","their","theirs","never",
"anyone","anybody","anything","anywhere","anytime","anyday","everyone",
"everybody","everything","everywhere","everytime","everyday","someone",
"somebody","something","somewhere","sometime","someday","noone","nobody",
"nothing","nowhere"]


class RestoredFunctionWordsFeatureExtractor(feature_extractor.FeatureExtractor):
  def ExtractFeaturesFromInstance(self, text, prompt, language, filename):
    counts = collections.defaultdict(int)

    insertions = collections.defaultdict(int)
    deletions = collections.defaultdict(int)
    matches = collections.defaultdict(int)

    corrected_filename = re.sub(r'/tokenized/', r'/corrected/', filename)
    predicted_filename = re.sub(r'/tokenized/', r'/restored_function_words/', filename)

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
          counts["Match_" + word] = math.log(matches[word])   
          counts["Match_p_" + word] = matches[word]/total

        if insertions[word] > 0:
          counts["Insert_" + word] = math.log(insertions[word]) 
          counts["Insert_p_" + word] = insertions[word]/total

        if deletions[word] > 0:
          counts["Delete_" + word] = math.log(deletions[word]) 
          counts["Delete_p_" + word] = deletions[word]/total

    return counts

if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_restored_function_words_features:
    return None
  return RestoredFunctionWordsFeatureExtractor()

