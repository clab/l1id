#!/bin/env python2.7

import sys, collections

LABELS = ["ARA", "CHI", "FRE", "GER", "HIN", "ITA", "JPN", "KOR", "SPA", "TEL", "TUR"]

def Split(in_file):
  weights = collections.defaultdict(list)
  out_files = {}

  for line in (in_file):
    tok = line.strip().split("\t")
    if len(tok) != 3:
      continue 
    weights[tok[0]].append((float(tok[2]), tok[1]))

  for label in LABELS:
    out_files[label] = open(label+".feat", "w")

  for label in LABELS:
    features = sorted(weights[label], key=lambda tup: abs(tup[0]), reverse = True) 
    for v, f in features:
      out_files[label].write("%f\t%s\n" % (v,f))

def main(args):
  usage = './split_n_sort.py in_weights'
  if len(args) < len(usage.split()) - 1:
    print 'usage:', usage
    sys.exit(1)

  Split(open(args[1]))

if __name__ == '__main__':
  main(sys.argv)


