#!/bin/bash

# Name of the experiment. This also defines which directory the output will be
# stored in (under ../data/work/).
export EXPERIMENT_NAME=most_frequent_words

# Which features should be extracted in this experiment.
# Set to 1 features you want to be included in this experiment.
export ADD_POS_FEATURES=0
export ADD_PUNCTUATION_FEATURES=0
export ADD_CFW_BIGRAMS_FEATURES=0
export ADD_CFW_TRIGRAMS_FEATURES=0
export ADD_CHARACTERS_FEATURES=0
export ADD_PRONOUNS_FEATURES=0
export ADD_REPETITIONS_FEATURES=0
export ADD_POSITIONAL_TOKEN_FEATURES=0
export ADD_RATIO_TO_PASSIVE_VERBS_FEATURES=0
export ADD_MEAN_WORD_RANKS_FEATURES=0
export ADD_MOST_FREQUENT_WORDS_FEATURES=1

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
  EXTRACT_FEATURES_PARAMS+="--append_pos_ngrams_features=True "
  EXTRACT_FEATURES_PARAMS+="--max_ngrams_order=3 "
  EXTRACT_FEATURES_PARAMS+="--pos_tagged_dir_ngrams=${POS_TAGGED_DIR} "
fi
if [ "${ADD_PUNCTUATION_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_punctuation_features=True "
fi
if [ "${ADD_CFW_BIGRAMS_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_cfw_bigrams_features=True "
  EXTRACT_FEATURES_PARAMS+="--pos_tagged_dir_cfw_bigrams=${POS_TAGGED_DIR} "
  EXTRACT_FEATURES_PARAMS+="--fw_bigrams_file=${FW_LIST_FILE} "
fi
if [ "${ADD_CFW_TRIGRAMS_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_cfw_trigrams_features=True "
  EXTRACT_FEATURES_PARAMS+="--pos_tagged_dir_cfw_trigrams=${POS_TAGGED_DIR} "
  EXTRACT_FEATURES_PARAMS+="--fw_trigrams_file=${FW_LIST_FILE} "
fi
if [ "${ADD_CHARACTERS_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_characters_ngrams_features=True "
  EXTRACT_FEATURES_PARAMS+="--characters_max_ngrams_order=3 "
fi
if [ "${ADD_PRONOUNS_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_pronouns_features=True "
fi
if [ "${ADD_REPETITIONS_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_repetitions_features=True "
  EXTRACT_FEATURES_PARAMS+="--pos_tagged_dir_repetitions=${POS_TAGGED_DIR} "
fi
if [ "${ADD_POSITIONAL_TOKEN_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_positional_token_frequencies=True "
fi
if [ "${ADD_RATIO_TO_PASSIVE_VERBS_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_ratio_to_passive_verbs=True "
  EXTRACT_FEATURES_PARAMS+="--pos_tagged_dir_passive_verbs=${POS_TAGGED_DIR} "
fi
if [ "${ADD_MEAN_WORD_RANKS_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_mean_word_ranks_features=True "
fi
if [ "${ADD_MOST_FREQUENT_WORDS_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_most_frequent_words_features=True "
  EXTRACT_FEATURES_PARAMS+="--most_frequent_words_num=10 "
fi

export EXTRACT_FEATURES_PARAMS
