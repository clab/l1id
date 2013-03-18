#!/usr/bin/python
""" Counts of restored cohesive verbs 
(only lemma, present contiguous/progressive and present simple ) 

our features - matches/insertions/deletions for each function word.
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

gflags.DEFINE_bool("append_restored_cohesive_verbs_features", False,
    "Append counts of restored cohesive verbs.")

FUNCTION_WORDS = ["indicate","indicates","indicating","signify","signifies","signifying",
"assume","assumes","assuming","emphasiz","emphasizes","emphasizing","emphasis",
"emphasises","emphasising","suggest","suggests","suggesting","demonstrate",
"demonstrates","demonstrating","reflect","reflects","reflecting","acknowledge",
"acknowledges","acknowledging","entails","entail","entailing","justify","justifies",
"justifying","entail","entails","entailing","state","states","stating","convey",
"conveying","conveys","perceive","perceives","perceiving","constitute","constitutes",
"constituting","reveals","reveal","revealing","assert","asserts","asserting","realize",
"realizes","realizing","explain","explains","explaininig","express","expresses",
"expressing","ensure","ensures","ensuring","demand","demands","demanding","affirm",
"affirms","affirming","highlight","highlights","highlighting","deny","denying",
"denies","predict","predicts","predicting","confirm","confirms","confirming",
"question","questions","questioning","exhibit","exhibits","exhibiting","represent",
"represents","representing","address","addresses","addressing","assure","assures",
"assuring","illustrate","illustrates","illustrating","prove","proves","proving",
"concern","concerns","concerning","achieve","achieves","achieving","note","notes",
"noting","guarantee","guarantees","guaranteeing","denote","denotes","denoting",
"stress","stresses","stressing","claims","claiming","claim","advocate",
"advocates","advocating","attribute","attributing","attributes","define",
"defines","defining","influence","influences","influencing","understand",
"understands","understanding","reinforce","reinforces","reinforcing","attain",
"attains","attaining","embrace","embraces","embracing","describe","describes",
"describing","ignore","ignores","ignoring","lack","lacks","lacking","recognise",
"recognises","recognising","recognize","recognizes","recognizing","reject",
"rejects","rejecting","embody","embodies","embodying","accomplish","accomplishes",
"accomplishing","require","requires","requiring","include","includes","including","allow",
"allows","allowing","involve","involves","involving","regard","regards",
"regarding","consider","considers","considering","provide","provides","providing",
"suggest","suggests","suggesting","show","shows","showing","keep","keeps","keeping",
"follow","follows","following","expect","expects","expecting","seems","seem","seeming",
"intend","intends","intending","seek","seeks","seeking","find","finding",
"finds","concern","concerns","concerning","represent","represents",
"representing","support","supports","supporting","manage","manages","managing",
"determine","determines","determining","add","adds","adding","mention","mentioning","mentions"]

class RestoredCohesiveVerbsFeatureExtractor(feature_extractor.FeatureExtractor):
  def ExtractFeaturesFromInstance(self, text, prompt, language, filename):
    counts = collections.defaultdict(int)

    insertions = collections.defaultdict(int)
    deletions = collections.defaultdict(int)
    matches = collections.defaultdict(int)

    corrected_filename = re.sub(r'/tokenized/', r'/corrected/', filename)
    predicted_filename = re.sub(r'/tokenized/', r'/restored_cohesion_verbs/', filename)

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
          counts["MatchCV_" + word] = math.log(matches[word])   
          counts["MatchCV_p_" + word] = matches[word]/total

        if insertions[word] > 0:
          counts["InsertCV_" + word] = math.log(insertions[word]) 
          counts["InsertCV_p_" + word] = insertions[word]/total

        if deletions[word] > 0:
          counts["DeleteCV_" + word] = math.log(deletions[word]) 
          counts["DeleteCV_p_" + word] = deletions[word]/total

    return counts

if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_restored_cohesive_verbs_features:
    return None
  return RestoredCohesiveVerbsFeatureExtractor()

