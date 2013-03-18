#!/usr/bin/python
""" Translationese feature:
Translations are known to excessively use certain cohesive markers (see list
below).
"""

import collections
import math
import sys
import gflags
import feature_extractor

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_cohesive_verbs_features", False,
    "Append counts of cohesive verbs")

COHESIVE_MARKERS = ["indicate",
"indicates",
"indicating",
"signify",
"signifies",
"signifying",
"assume",
"assumes",
"assuming",
"emphasiz",
"emphasizes",
"emphasizing",
"emphasis",
"emphasises",
"emphasising",
"suggest",
"suggests",
"suggesting",
"demonstrate",
"demonstrates",
"demonstrating",
"reflect",
"reflects",
"reflecting",
"acknowledge",
"acknowledges",
"acknowledging",
"entails",
"entail",
"entailing",
"justify",
"justifies",
"justifying",
"entail",
"entails",
"entailing",
"state",
"states",
"stating",
"convey",
"conveying",
"conveys",
"perceive",
"perceives",
"perceiving",
"constitute",
"constitutes",
"constituting",
"reveals",
"reveal",
"revealing",
"assert",
"asserts",
"asserting",
"realize",
"realizes",
"realizing",
"explain",
"explains",
"explaininig",
"express",
"expresses",
"expressing",
"ensure",
"ensures",
"ensuring",
"demand",
"demands",
"demanding",
"affirm",
"affirms",
"affirming",
"highlight",
"highlights",
"highlighting",
"deny",
"denying",
"denies",
"predict",
"predicts",
"predicting",
"confirm",
"confirms",
"confirming",
"question",
"questions",
"questioning",
"exhibit",
"exhibits",
"exhibiting",
"represent",
"represents",
"representing",
"address",
"addresses",
"addressing",
"assure",
"assures",
"assuring",
"illustrate",
"illustrates",
"illustrating",
"prove",
"proves",
"proving",
"concern",
"concerns",
"concerning",
"achieve",
"achieves",
"achieving",
"note",
"notes",
"noting",
"guarantee",
"guarantees",
"guaranteeing",
"denote",
"denotes",
"denoting",
"stress",
"stresses",
"stressing",
"claims",
"claiming",
"claim",
"advocate",
"advocates",
"advocating",
"attribute",
"attributing",
"attributes",
"define",
"defines",
"defining",
"influence",
"influences",
"influencing",
"understand",
"understands",
"understanding",
"reinforce",
"reinforces",
"reinforcing",
"attain",
"attains",
"attaining",
"embrace",
"embraces",
"embracing",
"describe",
"describes",
"describing",
"ignore",
"ignores",
"ignoring",
"lack",
"lacks",
"lacking",
"recognise",
"recognises",
"recognising",
"recognize",
"recognizes",
"recognizing",
"reject",
"rejects",
"rejecting",
"embody",
"embodies",
"embodying",
"accomplish",
"accomplishes",
"accomplishing",
"require",
"requires",
"requiring",
"include",
"includes",
"including",
"allow",
"allows",
"allowing",
"involve",
"involves",
"involving",
"regard",
"regards",
"regarding",
"consider",
"considers",
"considering",
"provide",
"provides",
"providing",
"suggest",
"suggests",
"suggesting",
"show",
"shows",
"showing",
"keep",
"keeps",
"keeping",
"follow",
"follows",
"following",
"expect",
"expects",
"expecting",
"seems",
"seem",
"seeming",
"intend",
"intends",
"intending",
"seek",
"seeks",
"seeking",
"find",
"finding",
"finds",
"concern",
"concerns",
"concerning",
"represent",
"represents",
"representing",
"support",
"supports",
"supporting",
"manage",
"manages",
"managing",
"determine",
"determines",
"determining",
"add",
"adds",
"adding",
"mention",
"mentioning",
"mentions"]


class CohesiveVerbsFeatureExtractor(feature_extractor.FeatureExtractor):
  def ExtractFeaturesFromInstance(self, text, prompt, language, filename):
    counts = collections.defaultdict(int)
    normalized_text = ' '.join(text.lower().split())
    for marker in COHESIVE_MARKERS:
      count = normalized_text.count(marker)
      if count > 0 :
        counts["CohVerb_" + marker] = count 

    # Normalize
    for feature, count in counts.iteritems():
      counts[feature] = math.log(count + 1)
    return counts
   
if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_cohesive_verbs_features:
    return None
  return CohesiveVerbsFeatureExtractor()

