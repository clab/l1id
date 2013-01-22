#!/bin/env python2.7

import sys
import importlib   # This requires at least python2.7
import glob
import os

"""All feature extractors should inherit from FeatureExtractor and also
   implement a top-level function named REGISTER_FEATURE_EXTRACTOR that returns
   an instance of the inherited class or None if the feature is disabled.
"""

class FeatureExtractor:
  def ExtractFeaturesFromInstance(self, text, language, filename):
    """Extracts features from a single input file.
       Params:
         text   - Text of the file.
         language - one of ["ARA", "HIN", "FRE", "SPA", ....]
         filename - path to the input file.

       Returns:
         Python dictionary where keys are feature names and values are numeric
         feature values (use (0,1) for booleans or just floats or integers).
    """
    pass


"""The code below dynamically imports all modules from the ./feature_extractors
   directory and provides a method for getting all enabled feature extractors:
   feature_extractor.Repository.GetActiveFeatureExtractors()
"""

# All feature extractors must reside in this sub-directory
FEATURE_EXTRACTORS_DIR = "feature_extractors"

class FeatureExtractorRepo:
  def __init__(self, base_dir):
    """Call this method before initializing FLAGS in main()"""
    self.feature_extractors = None
    self.modules = self._ImportAllModules(base_dir, FEATURE_EXTRACTORS_DIR)

  def _ImportAllModules(self, base_dir, module_dir):
    """Dynamically imports all python modules from the 'module_dir' directory.
    """
    modules = []
    feature_extractors_pattern = os.path.join(base_dir, module_dir, "*.py")
    for feature_extractor_filename in glob.iglob(feature_extractors_pattern):
      # Name of the python script without the .py suffix.
      module_name = os.path.splitext(
          os.path.basename(feature_extractor_filename))[0]
      module = importlib.import_module(module_dir + "." + module_name)
      if "REGISTER_FEATURE_EXTRACTOR" not in dir(module):
        continue
      modules.append(module)
    return modules

  def GetActiveFeatureExtractors(self):
    """Obtains FeatureExtractor objects from all the modules.
       Call this method after FLAGS are initialized in main()
    """
    if self.feature_extractors is None:
      self.feature_extractors = []
      for module in self.modules:
        extractor = module.REGISTER_FEATURE_EXTRACTOR()
        if extractor is None:
          print "Module:", module.__name__, "- Disabled"
        else:
          print "Module:", module.__name__, "- Enabled"
          if not isinstance(extractor, FeatureExtractor):
            print "Incorrect FeatureExtractor in module:", module.__name__
          self.feature_extractors.append(extractor)
    return self.feature_extractors


if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

# Import feature extractor modules *before* FLAGS are initialized to allow
# those modules to define their flags.
Repository = FeatureExtractorRepo(os.path.dirname(sys.argv[0]))
