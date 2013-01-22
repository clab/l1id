#!/bin/bash

# This script runs all preprocessing steps, extracts training features in Creg
# format, then it runs a 10-fold CV.
#
# You need to set paths to external tools such as Creg, StanfordPosTagger in the
# ./local_config.sh script.
#
# To configure experiment settings create a configuration file in the ./config/
# directory. See ./config/params.sh as an example. The configuration file is
# just a shell script that sets some variables that we use here, such as what
# features to use.
# Pass the name of the configuration file as a parameter to this script, or set
# an environment variable PARAMS (this is useful when running under qsub).
# By default it will use ./config/params.sh.

export ROOT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

if [ "$#" == "1" ] ; then
  PARAMS="$1"
fi
if [ -z $PARAMS ] ; then
  export PARAMS=${ROOT_DIR}/config/params.sh
fi

source ${ROOT_DIR}/local_config.sh
source $PARAMS

echo "-------------------------------------------------------------------------"
echo "Running experiment: ${EXPERIMENT_NAME}"
mkdir -p ${EXPERIMENT_DIR}
echo "Working directory is ${EXPERIMENT_DIR}"

echo "-------------------------------------------------------------------------"
echo "Preprocessing..."
if [ "${ADD_POS_FEATURES}" == "1" ] ; then
  ${BIN_DIR}/run_pos_tagger.sh ${TRAINING_INPUT_DIR} ${POS_TAGGED_DIR}
fi
#${BIN_DIR}/run_turbo_tagger.sh ${TRAINING_INPUT_DIR} ${TURBO_TAGGED_DIR}


echo "-------------------------------------------------------------------------"
echo "Extracting features"

# Creates a file with features and a file with labels in Creg format.
${BIN_DIR}/extract_instances.py --input_dir="${TRAINING_INPUT_DIR}" \
  --features_filename="${TRAIN_FEATURES}" --labels_filename="${TRAIN_LABELS}" \
  ${EXTRACT_FEATURES_PARAMS}


echo "-------------------------------------------------------------------------"
echo "Running Cross Validation (creg)"
#${BIN_DIR}/cross_validation.sh ${TRAIN_FEATURES} ${TRAIN_LABELS}
