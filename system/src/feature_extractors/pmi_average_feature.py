#!/usr/bin/python

import collections
import sys
import os
import codecs
import gflags
import feature_extractor
import re
import cPickle as pickle
import math
from decimal import *

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_pmi_average_features", False,
    "Append pmi average")
gflags.DEFINE_string("pmi_average_unigrams_dump", "",
    "PMI google 1-grams dump file")
gflags.DEFINE_string("pmi_average_bigrams_dump", "",
    "PMI google 2-grams dump files root directory")
gflags.DEFINE_integer("pmi_average_unigrams_number", 1,
    "PMI google 1-grams corpus size")
gflags.DEFINE_integer("pmi_average_bigrams_number", 1,
    "PMI google 2-grams corpus size")

""".
"""
class PMIAverageFeatureExtractor(feature_extractor.FeatureExtractor):
  def __init__(self, pmi_unigrams_dump,pmi_bigrams_dump,pmi_unigrams_number,pmi_bigrams_number):
    self.pmi_bigrams_dumps={}
    self.pmi_unigrams_dump = self.LoadPKFile(pmi_unigrams_dump)
    self.pmi_unigrams_number = pmi_unigrams_number
    self.pmi_bigrams_number = pmi_bigrams_number
    bigrams_dupms_dir=os.listdir(pmi_bigrams_dump)
    for gm2_file in bigrams_dupms_dir:
          if gm2_file.endswith(".pk"):
            self.pmi_bigrams_dumps[gm2_file]=self.LoadPKFile(pmi_bigrams_dump+gm2_file)
    getcontext().prec = 28
  
  def ExtractFeaturesFromInstance(self, text, prompt, language, filename):
    counts = collections.defaultdict(int)
    total = 1
    pmi_sum=0
    text= re.split('\n', text)
    for line in text:
      line=re.split('\s', line)
      prev=""
      pmi=0
      for token in line:
        if prev!="":
          tmp = self.pmi_unigrams_dump.setdefault(prev,0)# setdefault check if key exist in o(1) instead o(n)
          if tmp!=0:
            tmp = self.pmi_unigrams_dump.setdefault(token,0)
            if tmp!=0:
              bigram=(prev,token)
              google_2gms=self.SearchFiles(bigram[0])
              for gm2_file in google_2gms:
                gm2_file=gm2_file+".pk"
                tmp = self.pmi_bigrams_dumps[gm2_file].setdefault(bigram,0)
                if tmp!=0:
                  t1_freq=Decimal(self.pmi_unigrams_dump[prev])/Decimal(self.pmi_unigrams_number)
                  t2_freq=Decimal(self.pmi_unigrams_dump[token])/Decimal(self.pmi_unigrams_number)
                  t1t2_freq=Decimal(self.pmi_bigrams_dumps[gm2_file][bigram])/Decimal(self.pmi_bigrams_number) 
                  pmi=math.log(Decimal(t1t2_freq)/Decimal(t1_freq*t2_freq),2)  #PMI value
                  pmi_sum+=pmi
                  total+=1
        prev=token
    #if total!=0:
    return { "PMI": float(pmi_sum) / total }
    
  
  def LoadPKFile(self,filename):
    with open(filename, "r") as f:
      print("Load Pickle file: "+filename)
      return pickle.load(f)
    
  
  def SearchFiles(self,token):#indexing to google 2-grams files, to make the search faster
    if token.startswith('a'):
      return['2gm-0020','2gm-0021']
    elif token.startswith('b'):
      return['2gm-0021']
    elif token.startswith('c'):
      return['2gm-0021','2gm-0022']
    elif token.startswith('d'):
      return['2gm-0022','2gm-0023']
    elif token.startswith('e'):
      return['2gm-0023']
    elif token.startswith('f'):
      return['2gm-0023','2gm-0024']
    elif token.startswith('g'):
      return['2gm-0024']
    elif token.startswith('h'):
      return['2gm-0024']
    elif token.startswith('i'):
      return['2gm-0024','2gm-0025']
    elif token.startswith('j'):
      return['2gm-0025']     
    elif token.startswith('k'):
      return['2gm-0025']    
    elif token.startswith('l'):
      return['2gm-0025']
    elif token.startswith('m'):
      return['2gm-0025','2gm-0026']
    elif token.startswith('n'):
      return['2gm-0026']
    elif token.startswith('o'):
      return['2gm-0026','2gm-0027']
    elif token.startswith('p'):
      return['2gm-0027']
    elif token.startswith('q'):
      return['2gm-0027']
    elif token.startswith('r'):
      return['2gm-0027','2gm-0028']
    elif token.startswith('s'):
      return['2gm-0028','2gm-0029']
    elif token.startswith('t'):
      return['2gm-0029','2gm-0030'] 
    elif token.startswith('u'):
      return['2gm-0030']
    elif token.startswith('v'):
      return['2gm-0030']
    elif token.startswith('w'):
      return['2gm-0030','2gm-0031']
    elif token.startswith('x'):
      return['2gm-0031']
    elif token.startswith('y'):
      return['2gm-0031']
    elif token.startswith('z'):
      return['2gm-0031']
      
      
    elif token.startswith('A'):
      return['2gm-0006','2gm-0007']
    elif token.startswith('B'):
      return['2gm-0007','2gm-0008']
    elif token.startswith('C'):
      return['2gm-0008','2gm-0009']
    elif token.startswith('D'):
      return['2gm-0009','2gm-0010']
    elif token.startswith('E'):
      return['2gm-0010']
    elif token.startswith('F'):
      return['2gm-0010','2gm-0011']
    elif token.startswith('G'):
      return['2gm-0011']
    elif token.startswith('H'):
      return['2gm-0011','2gm-0012']
    elif token.startswith('I'):
      return['2gm-0012']
    elif token.startswith('J'):
      return['2gm-0012','2gm-0013']    
    elif token.startswith('K'):
      return['2gm-0013']    
    elif token.startswith('L'):
      return['2gm-0013']
    elif token.startswith('M'):
      return['2gm-0013','2gm-0014']
    elif token.startswith('N'):
      return['2gm-0014','2gm-0015']
    elif token.startswith('O'):
      return['2gm-0015']
    elif token.startswith('P'):
      return['2gm-0015','2gm-0016'] 
    elif token.startswith('Q'):
      return['2gm-0016']
    elif token.startswith('R'):
      return['2gm-0016','2gm-0017']
    elif token.startswith('S'):
      return['2gm-0017','2gm-0018']
    elif token.startswith('T'):
      return['2gm-0018']
    elif token.startswith('U'):
      return['2gm-0018','2gm-0019']
    elif token.startswith('V'):
      return['2gm-0019']
    elif token.startswith('W'):
      return['2gm-0019']
    elif token.startswith('X'):
      return['2gm-0019']
    elif token.startswith('Y'):
      return['2gm-0019']
    elif token.startswith('Z'):
      return['2gm-0019','2gm-0020']
      
    elif token.startswith('0'):
      return['2gm-0001','2gm-0002']
    elif token.startswith('1'):
      return['2gm-0002','2gm-0003']
    elif token.startswith('2'):
      return['2gm-0003','2gm-0004']
    elif token.startswith('3'):
      return['2gm-0004']
    elif token.startswith('4'):
      return['2gm-0004']
    elif token.startswith('5'):
      return['2gm-0004']
    elif token.startswith('6'):
      return['2gm-0004','2gm-0005']
    elif token.startswith('7'):
      return['2gm-0005']
    elif token.startswith('8'):
      return['2gm-0005']
    elif token.startswith('9'):
      return['2gm-0005']     
    else:
      return['2gm-0000','2gm-0001','2gm-0005','2gm-0006','2gm-0020','2gm-0031']
  
if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_pmi_average_features:
    return None
  if len(FLAGS.pmi_average_unigrams_dump) == 0:
    print "Flag --pmi_unigrams_dump is required"
    sys.exit(1)
  if len(FLAGS.pmi_average_bigrams_dump) == 0:
    print "Flag --pmi_bigrams_dump is required"
    sys.exit(1)
  return PMIAverageFeatureExtractor(FLAGS.pmi_average_unigrams_dump,FLAGS.pmi_average_bigrams_dump,FLAGS.pmi_average_unigrams_number,FLAGS.pmi_average_bigrams_number)
  
