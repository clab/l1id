#!/bin/env python2.7

import sys
import gflags
import random

"""Generates Test and Training files for a single fold of Cross Validation.
"""

FLAGS = gflags.FLAGS

gflags.DEFINE_integer("fold_num", 0, "Current fold number")

gflags.DEFINE_integer("total_folds_num", 10, "Number of folds")

gflags.DEFINE_integer("seed", 1234567, "Seed for random generator")

gflags.DEFINE_string("in_data_file", None,
    "Input file in creg format to cut into folds")
gflags.MarkFlagAsRequired("in_data_file")

gflags.DEFINE_string("out_train_file", None,
    "Output file in creg format for training")
gflags.MarkFlagAsRequired("out_train_file")

gflags.DEFINE_string("out_test_file", None,
    "Output file in creg format for test")
gflags.MarkFlagAsRequired("out_test_file")


def main(argv):
  try:
    argv = FLAGS(argv)  # parse flags
  except gflags.FlagsError, e:
    print '%s\nUsage: %s\n%s' % (e, sys.argv[0], FLAGS)
    sys.exit(1)

  random.seed(FLAGS.seed)
  train_file = open(FLAGS.out_train_file, "w")
  test_file = open(FLAGS.out_test_file, "w")
  for line in open(FLAGS.in_data_file):
    if random.randint(0, FLAGS.total_folds_num-1) == FLAGS.fold_num:
      test_file.write(line)
    else:
      train_file.write(line)

if __name__ == '__main__':
  main(sys.argv)


