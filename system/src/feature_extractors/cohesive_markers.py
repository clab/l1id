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

gflags.DEFINE_bool("append_cohesive_markers_features", False,
    "Append counts of cohesive markers")

COHESIVE_MARKERS = ["as for",
"as to",
"because",
"besides",
"but",
"consequently",
"despite",
"even if",
"even though",
"except",
"further",
"furthermore",
"hence",
"however",
"in addition",
"in conclusion",
"in other words",
"in spite",
"instead",
"is to say",
"maybe",
"moreover",
"nevertheless",
"on account of",
"on the contrary",
"on the other hand",
"otherwise",
"referring to",
"since",
"so",
"the former",
"the latter",
"therefore",
"this implies",
"though",
"thus",
"with reference to",
"with regard to",
"yet",
"concerning"]


class CohesiveMarkersFeatureExtractor(feature_extractor.FeatureExtractor):
  def ExtractFeaturesFromInstance(self, text, prompt, language, filename):
    counts = collections.defaultdict(int)
    normalized_text = ' '.join(text.lower().split())
    for marker in COHESIVE_MARKERS:
      count = normalized_text.count(marker)
      if count > 0 :
        counts["CohMarker_" + marker] = count 

    # Normalize
    for feature, count in counts.iteritems():
      counts[feature] = math.log(count + 1)
    return counts
   
if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_cohesive_markers_features:
    return None
  return CohesiveMarkersFeatureExtractor()

