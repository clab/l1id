#!/usr/bin/python

""" Ngram counts based on relation of brown clusters and corrected words
"""

import collections
import math
import sys
import gflags
import feature_extractor
import os 
import re 

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_brown_ngrams", False,
    "Append Brown ngrams counts")

gflags.DEFINE_integer("max_brown_ngrams_order", 2,
    "Maximal order of n-gram counts")

gflags.DEFINE_integer("max_brown_prefix_length", 16,
    "Maximal prefix of cluster number")

gflags.DEFINE_string("brown_clusters_filename", "",
    "Path to the file with Brown clusters")

gflags.DEFINE_integer("brown_freq_filter", 0,
    "filter min number of occurrences")

gflags.DEFINE_string("corrected_ngram_counts_prefix", "",
    "Path to the files with ngram-count ouputs for different ngram orders: all_corrected.1grams, all_corrected.2grams, all_corrected.3grams, all_corrected.4grams")

class BrownNgramsFeatureExtractor(feature_extractor.FeatureExtractor):
  def __init__(self, brown_clusters_filename, max_ngrams_order):
    self.words =  collections.defaultdict(lambda: '0')
    for line in open(brown_clusters_filename):
      # 111111101010	love	265727
      cluster, word, score = line.split('\t')
      self.words[word] = cluster
    self.max_ngrams_order = max_ngrams_order

  
  def ExtractFeaturesFromInstance(self, text, prompt, language, filename):
    result = {}
    for order in xrange(1, self.max_ngrams_order+1):
      result.update(self.ExtractFeaturesForOrder(order, filename))
    return result

  def LoadNgramCounts (self, filename):
    ngrams = set()
    for line in open(filename):
      ngram, count = line.strip().split('\t')
      count = int(count) 
      if count >= FLAGS.brown_freq_filter:
        ngrams.add(ngram.replace(" ", "_"))
    return ngrams
    
  def ExtractFeaturesForOrder(self, order, filename):
    if order > 2 :
      corpus_ngrams = self.LoadNgramCounts (FLAGS.corrected_ngram_counts_prefix + str(order) + "grams")
    corrected_filename = re.sub(r'/tokenized/', r'/corrected/', filename)
    counts = collections.defaultdict(int)
    total = 0
    for line in open(corrected_filename):
      words = line.split()
      clusters = [self.words[word][:FLAGS.max_brown_prefix_length] for word in words]
      
      for index in xrange(len(clusters)-order+1):
        cluster_ngram = clusters[index:index+order]
        counts["_".join(cluster_ngram)] += 1
        total += 1

    total = float(total)
    all_counts = collections.defaultdict(int)
    # Normalize to probabilities
    for feature, count in counts.iteritems():
      if order > 2 and feature in corpus_ngrams:
        #print "filtered: ", feature
        continue
      all_counts["B_p_" + feature] = count/total
      all_counts["B_" + feature] = math.log(count + 1)
    return all_counts

if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_brown_ngrams:
    return None
  if len(FLAGS.brown_clusters_filename) == 0:
    print "Flag --brown_clusters_filename is required"
    sys.exit(1)
  return BrownNgramsFeatureExtractor(FLAGS.brown_clusters_filename, FLAGS.max_brown_ngrams_order)

