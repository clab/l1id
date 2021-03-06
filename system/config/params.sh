#!/bin/bash

# Name of the experiment. This also defines which directory the output will be
# stored in (under ../data/work/).
export EXPERIMENT_NAME=brown_clusters_1grams_min10

# Which features should be extracted in this experiment.
# Set to 1 features you want to be included in this experiment.
export ADD_POS_FEATURES=0
export ADD_PUNCTUATION_FEATURES=0
export ADD_CFW_BIGRAMS_FEATURES=0
export ADD_CFW_TRIGRAMS_FEATURES=0
export ADD_CHARACTERS_FEATURES=0
export ADD_CHARACTERS_PROMPT_FEATURES=0
export ADD_MORE_FREQUENT_CHARACTERS_FEATURES=0
export ADD_PRONOUNS_FEATURES=0
export ADD_REPETITIONS_FEATURES=0
export ADD_POSITIONAL_TOKEN_FEATURES=0
export ADD_RATIO_TO_PASSIVE_VERBS_FEATURES=0
export ADD_MEAN_WORD_RANKS_FEATURES=0
export ADD_MOST_FREQUENT_WORDS_FEATURES=0
#export ADD_PMI_AVERAGE_FEATURES=0
export ADD_PMI_FEATURES=0
export ADD_DOCUMENT_LENGTH_FEATURES=1
export ADD_COHESIVE_MARKERS_FEATURES=0
export ADD_COHESIVE_VERBS_FEATURES=0
export ADD_MISSPELLINGS_FEATURES=0
export ADD_RESTORED_FW_FEATURES=0
export ADD_RESTORED_PUNCTUATION_FEATURES=0
export ADD_RESTORED_CV_FEATURES=0
export ADD_LEMMAS_FEATURES=0
export ADD_BROWN_NGRAMS_FEATURES=1

# Directory with training texts
export TRAINING_INPUT_DIR=${INPUT_DIR}/NLI_2013_Training_Data/tokenized
export CORRECTED_DIR=${INPUT_DIR}/NLI_2013_Training_Data/corrected
export TRAINING_INPUT_INDEX_FILE=${INPUT_DIR}/NLI_2013_Training_Data/index-training.csv
export EXPERIMENT_DIR=${WORK_DIR}/${EXPERIMENT_NAME}
export FW_LIST_FILE=${INPUT_DIR}/function_words.txt
export PMI_UNIGRAMS_DUMP=${INPUT_DIR}/1gms/vocab.pk
export PMI_BIGRAMS_DUMP=${INPUT_DIR}/2gms/
export BROWN_CLUSTERS=${INPUT_DIR}/en-c600.txt
export CORRECTED_NGRAM_COUNTS_PREFIX=${INPUT_DIR}/NLI_2013_Training_Data/all_brown.

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
if [ "${ADD_MISSPELLINGS_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--misspelling_features=True "
  EXTRACT_FEATURES_PARAMS+="--misspelling_extractor=${BIN_DIR}/spell-diff.pl "
fi
if [ "${ADD_RESTORED_FW_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_restored_function_words_features=True "
fi
if [ "${ADD_RESTORED_PUNCTUATION_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_restored_punctuation_features=True "
fi
if [ "${ADD_RESTORED_CV_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_restored_cohesive_verbs_features=True "
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
if [ "${ADD_CHARACTERS_PROMPT_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_characters_ngrams_prompt_features=True "
  EXTRACT_FEATURES_PARAMS+="--characters_max_ngrams_prompt_order=3 "
fi
if [ "${ADD_MORE_FREQUENT_CHARACTERS_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_more_frequent_characters_ngrams_features=True "
  EXTRACT_FEATURES_PARAMS+="--more_frequent_characters_ngrams_input_dir=${TRAINING_INPUT_DIR} "
  EXTRACT_FEATURES_PARAMS+="--more_frequent_characters_max_ngrams_order=3 "
  EXTRACT_FEATURES_PARAMS+="--frequency_limit=5 "
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
  EXTRACT_FEATURES_PARAMS+="--most_frequent_words_num=100 "
fi
#if [ "${ADD_PMI_AVERAGE_FEATURES}" == "1" ] ; then
#  EXTRACT_FEATURES_PARAMS+="--append_pmi_average_features=True "
#  EXTRACT_FEATURES_PARAMS+="--pmi_average_unigrams_dump=${PMI_UNIGRAMS_DUMP} "
#  EXTRACT_FEATURES_PARAMS+="--pmi_average_bigrams_dump=${PMI_BIGRAMS_DUMP} "
#  EXTRACT_FEATURES_PARAMS+="--pmi_average_unigrams_number=1024908267229 "
#  EXTRACT_FEATURES_PARAMS+="--pmi_average_bigrams_number=910868505431 "
#fi
if [ "${ADD_PMI_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_pmi_features=True "
  EXTRACT_FEATURES_PARAMS+="--pmi_unigrams_dump=${PMI_UNIGRAMS_DUMP} "
  EXTRACT_FEATURES_PARAMS+="--pmi_bigrams_dump=${PMI_BIGRAMS_DUMP} "
  EXTRACT_FEATURES_PARAMS+="--pmi_unigrams_number=1024908267229 "
  EXTRACT_FEATURES_PARAMS+="--pmi_bigrams_number=910868505431 "
  EXTRACT_FEATURES_PARAMS+="--pmi_threshold=0 "
fi
if [ "${ADD_DOCUMENT_LENGTH_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_document_length=True "
fi
if [ "${ADD_COHESIVE_MARKERS_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_cohesive_markers_features=True "
fi
if [ "${ADD_COHESIVE_VERBS_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_cohesive_verbs_features=True "
fi
if [ "${ADD_LEMMAS_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_lemmas_features=True "
  EXTRACT_FEATURES_PARAMS+="--pos_tagged_dir_lemmas=${POS_TAGGED_DIR} "
  EXTRACT_FEATURES_PARAMS+="--most_frequent_lemmas_num=300 "
fi
if [ "${ADD_BROWN_NGRAMS_FEATURES}" == "1" ] ; then
  EXTRACT_FEATURES_PARAMS+="--append_brown_ngrams=True "
  EXTRACT_FEATURES_PARAMS+="--max_brown_ngrams_order=4 "
  EXTRACT_FEATURES_PARAMS+="--max_brown_prefix_length=16 "
  EXTRACT_FEATURES_PARAMS+="--brown_clusters_filename=${BROWN_CLUSTERS} "
  EXTRACT_FEATURES_PARAMS+="--brown_freq_filter=100 "
  EXTRACT_FEATURES_PARAMS+="--corrected_ngram_counts_prefix=${CORRECTED_NGRAM_COUNTS_PREFIX} "
fi
export EXTRACT_FEATURES_PARAMS
