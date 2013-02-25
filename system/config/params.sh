#!/bin/bash

# Name of the experiment. This also defines which directory the output will be
# stored in (under ../data/work/).
export EXPERIMENT_NAME=all_features

# Which features should be extracted in this experiment.
# Set to 1 features you want to be included in this experiment.
export ADD_POS_FEATURES=0
export ADD_PUNCTUATION_FEATURES=1
export ADD_CONTEXTUAL_FW_FEATURES=0


# Directory with training texts
export TRAINING_INPUT_DIR=${INPUT_DIR}/NLI_2013_Training_Data/tokenized
export TRAINING_INPUT_INDEX_FILE=${INPUT_DIR}/NLI_2013_Training_Data/index-training.csv
export EXPERIMENT_DIR=${WORK_DIR}/${EXPERIMENT_NAME}
export FW_LIST_FILE=${INPUT_DIR}/function_words.txt

# Names of intermediate files:
export POS_TAGGED_DIR=${EXPERIMENT_DIR}/pos_tagged
export TURBO_TAGGED_DIR=${EXPERIMENT_DIR}/turbo_tagged
export TRAIN_FEATURES=${EXPERIMENT_DIR}/train.feat
export TRAIN_LABELS=${EXPERIMENT_DIR}/train.labels
export CROSS_VALIDATION_RESULTS=${EXPERIMENT_DIR}/train.results.txt

# The lines below create the command line parameters for the extract_instances.py
# based on the parameters above.
# NOTE: Don't forget to append an extra space after each parameter.
EXTRACT_FEATURES_PARAMS=""
if [ "${ADD_POS_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_pos_bigrams_features=True "
  EXTRACT_FEATURES_PARAMS+="--pos_tagged_dir=${POS_TAGGED_DIR} "
fi
if [ "${ADD_PUNCTUATION_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_punctuation_features=True "
fi
if [ "${ADD_CONTEXTUAL_FW_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_contextual_function_words_features=True "
  EXTRACT_FEATURES_PARAMS+="--fw_pos_tagged_dir=${POS_TAGGED_DIR} "
  EXTRACT_FEATURES_PARAMS+="--function_words_list_path=${FW_LIST_FILE} "
fi
export EXTRACT_FEATURES_PARAMS
