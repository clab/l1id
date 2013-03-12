#!/bin/bash

SOURCE_FEAT=$1
SOURCE_LABEL=$2

CROSS_VALIDATION_DIR=${EXPERIMENT_DIR}/cross_validation
mkdir -p ${CROSS_VALIDATION_DIR}

NUM_FOLDS=10
SEED=123456    # Seeds the random generator in the generate_folds.py.
for ((i = 0; i < NUM_FOLDS; i++)); do
  echo "Cross validation, fold: $i"
  OUT_FILE_PREFIX=${CROSS_VALIDATION_DIR}/fold_${i}
  TRAIN_FEAT=${OUT_FILE_PREFIX}.train.feat
  TRAIN_LABEL=${OUT_FILE_PREFIX}.train.label
  TEST_FEAT=${OUT_FILE_PREFIX}.test.feat
  TEST_LABEL=${OUT_FILE_PREFIX}.test.label
  OUT_WEIGHTS=${OUT_FILE_PREFIX}.weights
  PREDICTED_LABELS=${OUT_FILE_PREFIX}.test.predicted
  RESULTS=${OUT_FILE_PREFIX}.results.txt
  # Since Creg needs two separate files for Features and Labels, we run
  # generate_fold.py once for each of these files.
  ${BIN_DIR}/generate_fold.py --fold_num=$i --total_folds_num=${NUM_FOLDS} \
      --seed=${SEED} --in_data_file=${SOURCE_FEAT} \
      --out_train_file=${TRAIN_FEAT} --out_test_file=${TEST_FEAT}
  ${BIN_DIR}/generate_fold.py --fold_num=$i --total_folds_num=${NUM_FOLDS} \
      --seed=${SEED} --in_data_file=${SOURCE_LABEL} \
      --out_train_file=${TRAIN_LABEL} --out_test_file=${TEST_LABEL}
  # Now run Creg and store the results in a file.
  # If it fails, see the ${RESULTS} for error messages.
  ${CREG_BIN} -x ${TRAIN_FEAT} -y ${TRAIN_LABEL} --l2 1.0 --tx ${TEST_FEAT} \
      --ty ${TEST_LABEL} -D --z ${OUT_WEIGHTS} > ${PREDICTED_LABELS} 2> ${RESULTS}
done

# Now parse the results files from all the folds, calculate the Mean, Min and
# Max.
${BIN_DIR}/collect_results.py  --metadata_file="${TRAINING_INPUT_INDEX_FILE}" \
    --creg_results_file_pattern="${CROSS_VALIDATION_DIR}/fold_?.results.txt" \
    --creg_predictions_file_pattern="${CROSS_VALIDATION_DIR}/fold_?.test.predicted" \
    --out_file="${CROSS_VALIDATION_RESULTS}"

cat ${CROSS_VALIDATION_RESULTS}
