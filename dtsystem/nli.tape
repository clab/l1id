# ducttape workflow by nschneid@cs.cmu.edu

import config.tape


# tasks:
# 1. rawdata: concatenates all the documents for each L1
# 2. pos: runs POS tagging
# 3. extraction: feature extraction
# 4. partition: split each L1's data for cross-validation
# 5. xval: cross-validation
# 6. aggregate: compute statistics over all cross-validation folds, such as min, max, and mean accuracy (TODO)

global {
	tokdir=(Section: train="../system/data/input/NLI_2013_Training_Data/tokenized" dev="../system/data/input/NLI_2013_Development_Data/tokenized")
	  meta=(Section: train="../system/data/input/NLI_2013_Training_Data/index-training.csv" dev="../system/data/input/NLI_2013_Development_Data/index-dev.csv")
	 langs="ARA CHI FRE GER HIN ITA JPN KOR SPA TEL TUR"
}

task rawdata < tokdir=@ > emptyflags {

    #cat $tokdir/$lang/*.txt > $out
    
    #ln -s "$tokdir/$lang" "$langdir"	# effectively assigns to output variable
    
    echo "" > $emptyflags
}
task pos : pos_tagger 
         < tokdir=@ posdir=(Section: train="../system/data/work/pos_tagged_train" dev="../system/data/work/pos_tagged_dev")
         > flags {
  	for f in `ls $tokdir/*.txt` ; do
      fname=`basename $f`
	  if [ ! -s "$posdir/$fname" ] ; then
		echo "Running POS tagger on $f" > /dev/stderr
		java -mx300m -classpath $pos_tagger/stanford-postagger-3.1.4.jar edu.stanford.nlp.tagger.maxent.MaxentTagger -model $pos_tagger/models/english-left3words-distsim.tagger -textFile $f > "$posdir/$fname"
	  fi
	done
    
    echo "--pos_tagged_dir_ngrams=$posdir --pos_tagged_dir_cfw_bigrams=$posdir --pos_tagged_dir_cfw_trigrams=$posdir --pos_tagged_dir_repetitions=$posdir --pos_tagged_dir_passive_verbs=$posdir" > $flags
}
#task pos : pos_tagger < rawdata=out@rawdata > pos_tagged flags {
#    java -mx300m -classpath $pos_tagger/stanford-postagger-3.1.4.jar edu.stanford.nlp.tagger.maxent.MaxentTagger -model $pos_tagger/models/english-left3words-distsim.tagger -textFile $rawdata > $pos_tagged
#    
#    echo "--append_pos_bigrams_features=True --pos_tagged_FILE=$pos_tagged" > $pos_flags
#}

# Creates a file with labels and features
task extraction : scripts 
	< tokdir=@ meta=@ posdirflags=(PosFeatures: 0=$emptyflags@rawdata 1=$flags@pos) fxnwordslist=../system/data/input/function_words.txt
	> feats lbls out 
	:: punctflags=(PunctFeatures: 0="" 1="--append_punctuation_features=True")
	:: posflags=(PosFeatures: 0="" 1="--append_pos_ngrams_features=True --max_ngrams_order=3")
	:: cxtfxnflags=(CxtFxnFeatures: 0="" 1="--append_cfw_bigrams_features=True --append_cfw_trigrams_features=True") 
	:: charflags=(CharFeatures: 0="" 1="--append_characters_ngrams_features=True --characters_max_ngrams_order=3")
	:: pronflags=(PronFeatures: 0="" 1="--append_pronouns_features=True")
	:: repflags=(RepFeatures: 0="" 1="--append_repetitions_features=True")
	:: positionflags=(PositionFeatures: 0="" 1="--append_positional_token_frequencies=True") 
	:: psvratioflags=(PsvRatioFeatures: 0="" 1="--append_ratio_to_passive_verbs=True")
	:: wordrankflags=(WordRankFeatures: 0="" 1="--append_mean_word_ranks_features=True")
	:: freqwordflags=(FreqWordFeatures: 0="" 1="--append_most_frequent_words_features=True --most_frequent_words_num=10") {
    
    $scripts/extract_instances.py --input_dir="$tokdir" --metadata_filename="$meta" \
  --features_filename=$feats --labels_filename=$lbls `cat $posdirflags` $posflags $punctflags $cxtfxnflags $charflags $pronflags $repflags $positionflags --fw_bigrams_file="$fxnwordslist" --fw_trigrams_file="$fxnwordslist"
  
  	paste $lbls $feats > $out
  
}

task trainpredict : creg pyutil 
    < train_gold_lbls=$lbls@extraction[Section:train] train_feats=$feats@extraction[Section:train]
    < pred_gold_lbls=$lbls@extraction[Section:dev] pred_feats=$feats@extraction[Section:dev] 
    < pred_tokdir=$tokdir[Section:dev] pred_meta=$meta[Section:dev]
    	> idPreds preds weights results pred_tokdir_path pred_meta_path
    	:: reg=(Regularization: l2_1.0="--l2 1.0" l2_10.0="--l2 0.1" l2_0.5="--l2 0.5" l2_5.0="--l2 5.0" l1_1.0="--l1 1.0") {

    # Now run Creg and store the results in a file.
  	# If it fails, see the stderr.
  	$creg/dist/bin/creg -x $train_feats -y $train_gold_lbls $reg --tx $pred_feats > predsAndWeights
    
    NTEST=$(wc -l $pred_gold_lbls | cut -d' ' -f1)
    echo $NTEST
    
    # Separate predictions from weights
    # (avoid using `head` due to ducttape's handling of early termination)
    $pyutil/dataformats/distribute.py --align no  --stop $NTEST -n 1 predsAndWeights
    mv batch000 idPreds
    cut -f2 idPreds > preds
    cut -f1 $pred_gold_lbls > ids
    cut -f2 $pred_gold_lbls > golds
    # TODO: implement --align no option in distribute
    rm batch000.align
    $pyutil/dataformats/distribute.py --align no --start $NTEST -n 1 predsAndWeights
    mv batch000 weights
    rm batch000.align
    
    # TODO: implement IO scheme in chunkeval
    # Run evaluation script (requires B- to be prefixed to every label)
    paste ids <(sed -E 's/^(.+)$/B-\1/' golds) <(sed -E 's/^(.+)$/B-\1/' preds) > idGoldAndPreds
	$pyutil/chunkeval.py BIO idGoldAndPreds > results
	
	echo $pred_tokdir > pred_tokdir_path
	echo $pred_meta > pred_meta_path
}

# Aggregate results over all folds and compute summary statistics
task eval : scripts < idPreds=@trainpredict weights=@trainpredict meta_path=$pred_meta_path@trainpredict > results {
	meta=`cat $meta_path`
	set -x
	$scripts/collect_results.py --creg_predictions_files "$idPreds" --metadata_file "$meta" \
		--creg_results_files "$weights" --out_file results
}

# Split into 10 partitions (by an arbitrary hash)
task partition : pyutil < data=$out@extraction[Section:train] > partitions :: FOLDS=4 {
	mkdir partitions
	cd partitions
    $pyutil/dataformats/distribute.py --pre-sort md5 -n $FOLDS --batch-start 0 --batch-width 1 $data
}



task xval : creg pyutil < partitions=@partition > idPreds preds weights results :: fold=(Fold: 0..3) {

	# test on <partition>/batch$fold
	echo "$partitions" | xargs ls | grep "^batch$fold\$" > test.files
	cat test.files | xargs -I {} cat "$partitions/{}" | cut -f1-2 > test.lbls
	cat test.files | xargs -I {} cat "$partitions/{}" | cut -f3- > test.feats
	
	# train on other folds matching <partition>/batch*
	echo "$partitions" | xargs ls | egrep '^batch[0-9]+$' | fgrep -v -f test.files > train.files
	cat train.files | xargs -I {} cat "$partitions/{}" | cut -f1-2 > train.lbls
	cat train.files | xargs -I {} cat "$partitions/{}" | cut -f3- > train.feats
	
    # Now run Creg and store the results in a file.
  	# If it fails, see the stderr.
  	$creg/dist/bin/creg -x train.feats -y train.lbls --l1 1.0 --tx test.feats > predsAndWeights
    
    NTEST=$(wc -l $partitions/batch$fold | cut -d' ' -f1)
    echo $NTEST
    
    # Separate predictions from weights
    # (avoid using `head` due to ducttape's handling of early termination)
    $pyutil/dataformats/distribute.py --align no  --stop $NTEST -n 1 predsAndWeights
    mv batch000 idPreds
    cut -f2 idPreds > preds
    cut -f1 test.lbls > ids
    cut -f2 test.lbls > golds
    # TODO: implement --align no option in distribute
    rm batch000.align
    $pyutil/dataformats/distribute.py --align no --start $NTEST -n 1 predsAndWeights
    mv batch000 weights
    rm batch000.align
    
    # TODO: implement IO scheme in chunkeval
    # Run evaluation script (requires B- to be prefixed to every label)
    paste ids <(sed -E 's/^(.+)$/B-\1/' golds) <(sed -E 's/^(.+)$/B-\1/' preds) > idGoldAndPreds
	$pyutil/chunkeval.py BIO idGoldAndPreds > results

}

# Aggregate results over all folds and compute summary statistics
task xeval : scripts < idPreds=@xval[Fold:*] weights=@xval[Fold:*] tokdir=$tokdir[Section:train] meta=$meta[Section:train] > results {
	set -x
	$scripts/collect_results.py --creg_predictions_files "$idPreds" --metadata_file "$meta" \
		--creg_results_files "$weights" --out_file results
}

summary fold_acc {
  of xval > FoldAcc {
	grep 'Exact L' $results | cut -d' ' -f7 > $FoldAcc
  }
}

summary dev_acc {
  of eval > DevAcc {
	grep 'Total accuracy:' $results | cut -d' ' -f3 > $DevAcc
  }
}

#task aggregate < results=@xval[Fold:*] {
	# TODO: mean, min, max over all folds? modify collect_results.py?
	# TODO: breakdown by L1?
#	false
#}

plan DevAllFeats {
	reach eval via (Regularization: l2_1.0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) * (RepFeatures: 1) 
		* (PositionFeatures: 1) * (PsvRatioFeatures: 1) * (WordRankFeatures: 1) 
		* (FreqWordFeatures: 1)
}
plan DevCharsReg {
	reach eval via (Regularization: l2_1.0 l2_0.1 l2_0.5 l2_5.0) * (PosFeatures: 0) * (PunctFeatures: 0) 
		* (CxtFxnFeatures: 0) * (CharFeatures: 1) * (PronFeatures: 0) * (RepFeatures: 0) 
		* (PositionFeatures: 0) * (WordRankFeatures: 0) 
		* (FreqWordFeatures: 0)
		 * (PsvRatioFeatures: 0) # apparently unhelpful
}
plan DevIncremental {
	reach eval via (Regularization: l2_1.0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 0) * (PronFeatures: 1) * 
		* (PositionFeatures: 1)
		* (FreqWordFeatures: 0) * (PsvRatioFeatures: 0) * (RepFeatures: 0) * (WordRankFeatures: 0)  # apparently unhelpful
}
