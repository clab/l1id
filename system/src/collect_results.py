#!/bin/env python2.7

import sys
import glob
import os
import re
import gflags
import collections

FLAGS = gflags.FLAGS

gflags.DEFINE_string("creg_results_file_pattern", None,
    "Directory with output files")
gflags.MarkFlagAsRequired("creg_results_file_pattern")

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

def CollectResults(creg_results_file_pattern, out_file):
  experiment_stats = []
  for filename in glob.glob(creg_results_file_pattern):
    accuracy = ReadAccuracy(filename)
    if accuracy is not None:
      experiment_stats.append(accuracy)
  if len(experiment_stats) > 0:
    out_file.write("{0}\t[{1}..{2}]\n".format(avg(experiment_stats), 
                   min(experiment_stats), max(experiment_stats)))

def main(argv):
  try:
    argv = FLAGS(argv)  # parse flags
  except gflags.FlagsError, e:
    print '%s\nUsage: %s\n%s' % (e, sys.argv[0], FLAGS)
    sys.exit(1)
  if FLAGS.out_file is not None:
    out_file = open(FLAGS.out_file, "a")
  else:
    out_file = sys.stdout

  CollectResults(FLAGS.creg_results_file_pattern, out_file)
 
if __name__ == '__main__':
  main(sys.argv)


