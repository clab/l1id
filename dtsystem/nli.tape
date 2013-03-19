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
	posdir=(Section: train="../system/data/work/pos_tagged_train" dev="../system/data/work/pos_tagged_dev")
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

task pmi < pmi_unigrams_dump=../system/data/input/1gms/vocab.pk pmi_bigrams_dump=../system/data/input/2gms/ 
         > pmidataflags {
	echo "--pmi_unigrams_dump=$pmi_unigrams_dump --pmi_bigrams_dump=$pmi_bigrams_dump --pmi_unigrams_number=1024908267229 --pmi_bigrams_number=910868505431" > $pmidataflags
}

# Creates a file with labels and features
task extraction : scripts 
	< tokdir=@ meta=@ posdirflags=(PosFeatures: 0=$emptyflags@rawdata 1=$flags@pos) 
	< pmidataflags=(PMIFeatures: 0=$emptyflags@rawdata 1=$pmidataflags@pmi)
	< fxnwordslist=../system/data/input/function_words.txt
	> feats lbls out 
	:: punctflags=(PunctFeatures: 0="" 1="--append_punctuation_features=True")
	:: posflags=(PosFeatures: 0="" 1="--append_pos_ngrams_features=True --max_ngrams_order=3")
	:: cxtfxnflags=(CxtFxnFeatures: 0="" 1="--append_cfw_bigrams_features=True --append_cfw_trigrams_features=True") 
	:: charflags=(CharFeatures: 0="" 1="--append_characters_ngrams_features=True --characters_max_ngrams_order=3")
	:: pronflags=(PronFeatures: 0="" 1="--append_pronouns_features=True")
	:: repflags=(Rep: 0="" 1="--append_repetitions_features=True")
	:: positionflags=(PositionFeatures: 0="" 1="--append_positional_token_frequencies=True") 
	:: psvratioflags=(PsvRatioFeatures: 0="" 1="--append_ratio_to_passive_verbs=True")
	:: wordrankflags=(WordRank: 0="" 1="--append_mean_word_ranks_features=True")
	:: freqwordflags=(FreqWord: 0="" 100="--append_most_frequent_words_features=True --most_frequent_words_num=100") 
	#1="--append_most_frequent_words_features=True --most_frequent_words_num=10" 50="--append_most_frequent_words_features=True --most_frequent_words_num=50"
    :: pmiflags=(PMIFeatures: 0="" 1="--append_pmi_features=True --pmi_threshold=0")
    :: doclenflags=(DocLenFeatures: 0="" 1="--append_document_length=True")
    :: cohesiveflags=(Coh: 0="" 1="--append_cohesive_markers_features=True")
    :: misspellflags=(MisspellFeatures: 0="" 1="--misspelling_features=True") {
    
    $scripts/extract_instances.py --input_dir="$tokdir" --metadata_filename="$meta" \
  --features_filename=$feats --labels_filename=$lbls `cat $posdirflags` `cat $pmidataflags` $punctflags $posflags $cxtfxnflags $charflags $pronflags $repflags $positionflags $psvratioflags $wordrankflags $freqwordflags $pmiflags $doclenflags $cohesiveflags $misspellflags --fw_bigrams_file="$fxnwordslist" --fw_trigrams_file="$fxnwordslist" --misspelling_extractor="$scripts/spell-diff.pl"
  
  	paste $lbls $feats > $out
  
}

task trainpredict2 : creg < train_golds=$lbls@extraction
  < train_feats1=(PunctFeatures: 0="/dev/null" 1=$feats@extraction[PosFeatures:0,CharFeatures:0])
  < train_feats2=(PosFeatures: 0="/dev/null" 1=$feats@extraction[PunctFeatures:0,CharFeatures:0])
  < train_feats3=(CharFeatures: 0="/dev/null" 1=$feats@extraction[PunctFeatures:0,PosFeatures:0])
  {
  	$creg/dist/bin/creg -y $train_gold_lbls -x $train_feats1 -x $train_feats2 -x $train_feats3 --z $weights $reg --tx $pred_feats > idPreds
  }

task extraction3 < tokdir=@ meta=@ > lbls feats1 feats2 feats3 {
  $scripts/extract_instances.py --input_dir="$tokdir" --metadata_filename="$meta" \
  --features_filename=$feats1 --labels_filename=$lbls --append_punctuation_features=True
  
  $scripts/extract_instances.py --input_dir="$tokdir" --metadata_filename="$meta" \
  --features_filename=$feats1 --labels_filename=$lbls --append_pos_ngrams_features=True --max_ngrams_order=3
  
  $scripts/extract_instances.py --input_dir="$tokdir" --metadata_filename="$meta" \
  --features_filename=$feats1 --labels_filename=$lbls --append_characters_ngrams_features=True --characters_max_ngrams_order=3
}

task trainpredict3 < train_golds=$lbls@extraction[Section:train]
    < train_feats1=(PunctFeatures: 0="/dev/null" 1=$feats1@extraction3)
    < train_feats2=(PosFeatures: 0="/dev/null" 1=$feats2@extraction3)
    < train_feats3=(CharFeatures: 0="/dev/null" 1=$feats3@extraction3)
  {
   	$creg/dist/bin/creg -y $train_gold_lbls -x $train_feats1 -x $train_feats2 -x $train_feats3 --z $weights $reg --tx $pred_feats > idPreds
  }

func extract_instances : scripts < tokdir posdir meta > lbl feats :: flags {
	$scripts/extract_instances.py --input_dir="$tokdir" --metadata_filename="$meta" \
  --features_filename=$feats --labels_filename=$lbls $flags --pos_tagged_dir_ngrams="$posdir" --pos_tagged_dir_repetitions=$posdir --pos_tagged_dir_passive_verbs="$posdir" --pos_tagged_dir_lemmas="$posdir" --misspelling_extractor="$scripts/spell-diff.pl" --more_frequent_characters_ngrams_input_dir="$tokdir"
}
# TODO: OMG should these be $tokdir and $posdir (branch dependent), or always the training directories???
task labels    calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats  :: flags=""
task _punct    calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_punctuation_features=True"
#task _pos      calls extract_instances > feats :: flags="--append_pos_ngrams_features=True --max_ngrams_order=3"
task _char     calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_characters_ngrams_features=True --characters_max_ngrams_order=3"
#task _cxtfxn   calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_cfw_bigrams_features=True --append_cfw_trigrams_features=True --fw_bigrams_file=../system/data/input/function_words.txt --fw_trigrams_file=../system/data/input/function_words.txt"
task _pron     calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_pronouns_features=True"
task _rep      calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_repetitions_features=True"
task _position calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_positional_token_frequencies=True"
task _psvratio calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_ratio_to_passive_verbs=True"
task _wordrank calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_mean_word_ranks_features=True"
task _freqword calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_most_frequent_words_features=True --most_frequent_words_num=100"
#task _pmi      calls extract_instances > feats :: flags="--append_pmi_features=True --pmi_threshold=0"
task _doclen   calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_document_length=True"
task _cohesive calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_cohesive_markers_features=True"
task _cohverbs calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_cohesive_verbs_features=True"
task _misspell calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--misspelling_features=True"
task _lemmas   calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_lemmas_features=True --most_frequent_lemmas_num=300"
task _restfxn  calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_restored_function_words_features=True"
task _restpunc calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_restored_punctuation_features=True"
task _restcohverbs calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_restored_cohesive_verbs_features=True"
task _charprompt calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_characters_ngrams_prompt_features=True --characters_max_ngrams_prompt_order=3"
task _freqchar calls extract_instances : scripts < tokdir=@ posdir=@ meta=@ > lbls feats :: flags="--append_more_frequent_characters_ngrams_features=True  --more_frequent_characters_max_ngrams_order=4 --frequency_limit=5"
task _pos : pos_tagger scripts < tokdir=@ posdir=@ meta=@ > feats {
  	for f in `ls $tokdir/*.txt` ; do
      fname=`basename $f`
	  if [ ! -s "$posdir/$fname" ] ; then
		echo "Running POS tagger on $f" > /dev/stderr
		java -mx300m -classpath $pos_tagger/stanford-postagger-3.1.4.jar edu.stanford.nlp.tagger.maxent.MaxentTagger -model $pos_tagger/models/english-left3words-distsim.tagger -textFile $f > "$posdir/$fname"
	  fi
	done
    
    set -x
	$scripts/extract_instances.py --input_dir="$tokdir" --metadata_filename="$meta" \
  --features_filename=$feats --labels_filename=lbls --pos_tagged_dir_ngrams="$posdir" --append_pos_ngrams_features=True --max_ngrams_order=3
}
task _cxtfxn : scripts < tokdir=@ posdir=@ meta=@ fxnwords=../system/data/input/function_words.txt > feats {
	$scripts/extract_instances.py --input_dir="$tokdir" --metadata_filename="$meta" \
  --features_filename=$feats --labels_filename=lbls --append_cfw_bigrams_features=True --append_cfw_trigrams_features=True --fw_bigrams_file=$fxnwords --fw_trigrams_file=$fxnwords --pos_tagged_dir_cfw_bigrams="$posdir" --pos_tagged_dir_cfw_trigrams="$posdir" 
}
task _pmi : scripts < tokdir=@ meta=@ pmi_unigrams_dump=../system/data/input/1gms/vocab.pk pmi_bigrams_dump=../system/data/input/2gms/ 
         > feats {
    $scripts/extract_instances.py --input_dir="$tokdir" --metadata_filename="$meta" \
  --features_filename=$feats --labels_filename=lbls --append_pmi_features=True --pmi_threshold=0 --pmi_unigrams_dump=$pmi_unigrams_dump --pmi_bigrams_dump=$pmi_bigrams_dump --pmi_unigrams_number=1024908267229 --pmi_bigrams_number=910868505431
}
task _brown : scripts < tokdir=@ meta=@ brown_clusters=../system/data/input/en-c600.txt
         > feats {
    $scripts/extract_instances.py --input_dir="$tokdir" --metadata_filename="$meta" \
  --features_filename=$feats --labels_filename=lbls --append_brown_ngrams=True --max_brown_ngrams_order=3 --max_brown_prefix_length=16 --brown_clusters_filename=$brown_clusters
}

task train_predict : creg pyutil
	< train_gold_lbls=$lbls@labels[Section:train]
    < tf1=(PunctFeatures:   0="/dev/null" 1=$feats@_punct[Section:train])
    < tf2=(PosFeatures:     0="/dev/null" 1=$feats@_pos[Section:train])
    < tf3=(CharFeatures:    0="/dev/null" 1=$feats@_char[Section:train])
    < tf4=(CxtFxnFeatures:  0="/dev/null" 1=$feats@_cxtfxn[Section:train])
    < tf5=(PronFeatures:    0="/dev/null" 1=$feats@_pron[Section:train])
    < tf6=(Rep:     0="/dev/null" 1=$feats@_rep[Section:train])
    < tf7=(PositionFeatures:  0="/dev/null" 1=$feats@_position[Section:train])
    < tf8=(PsvRatioFeatures:  0="/dev/null" 1=$feats@_psvratio[Section:train])
    < tf9=(WordRank:  0="/dev/null" 1=$feats@_wordrank[Section:train])
    < tf10=(FreqWord: 0="/dev/null" 100=$feats@_freqword[Section:train]) 
    < tf11=(PMIFeatures:      0="/dev/null" 1=$feats@_pmi[Section:train])
    < tf12=(DocLenFeatures:   0="/dev/null" 1=$feats@_doclen[Section:train])
    < tf13=(Coh: 0="/dev/null" 1=$feats@_cohesive[Section:train])
    < tf14=(MisspellFeatures: 0="/dev/null" 1=$feats@_misspell[Section:train])
    < tf15=(CohVerbs: 0="/dev/null" 1=$feats@_cohverbs[Section:train])
    < tf16=(LemmaFeatures:  0="/dev/null" 1=$feats@_lemmas[Section:train])
    < tf17=(Restore:  0="/dev/null" 1=$feats@_restfxn[Section:train])
    < tf18=(Restore:  0="/dev/null" 1=$feats@_restpunc[Section:train])
    < tf19=(Restore:  0="/dev/null" 1=$feats@_restcohverbs[Section:train])
    < tf20=(CharPrompt: 0="/dev/null" 1=$feats@_charprompt[Section:train])
    < tf21=(FreqChar: 0="/dev/null" 1=$feats@_freqchar[Section:train])
    < tf22=(Brown: 0="/dev/null" 1=$feats@_brown[Section:train])
    < pred_meta=$meta[Section:dev]
    < predict_golds=$lbls@labels[Section:dev]
    < pf1=(PunctFeatures:   0="/dev/null" 1=$feats@_punct[Section:dev])
    < pf2=(PosFeatures:     0="/dev/null" 1=$feats@_pos[Section:dev])
    < pf3=(CharFeatures:    0="/dev/null" 1=$feats@_char[Section:dev])
    < pf4=(CxtFxnFeatures:  0="/dev/null" 1=$feats@_cxtfxn[Section:dev])
    < pf5=(PronFeatures:    0="/dev/null" 1=$feats@_pron[Section:dev])
    < pf6=(Rep:     0="/dev/null" 1=$feats@_rep[Section:dev])
    < pf7=(PositionFeatures:  0="/dev/null" 1=$feats@_position[Section:dev])
    < pf8=(PsvRatioFeatures:  0="/dev/null" 1=$feats@_psvratio[Section:dev])
    < pf9=(WordRank:  0="/dev/null" 1=$feats@_wordrank[Section:dev])
    < pf10=(FreqWord: 0="/dev/null" 100=$feats@_freqword[Section:dev]) 
    < pf11=(PMIFeatures:      0="/dev/null" 1=$feats@_pmi[Section:dev])
    < pf12=(DocLenFeatures:   0="/dev/null" 1=$feats@_doclen[Section:dev])
    < pf13=(Coh: 0="/dev/null" 1=$feats@_cohesive[Section:dev])
    < pf14=(MisspellFeatures: 0="/dev/null" 1=$feats@_misspell[Section:dev])
    < pf15=(CohVerbs: 0="/dev/null" 1=$feats@_cohverbs[Section:dev])
    < pf16=(LemmaFeatures:  0="/dev/null" 1=$feats@_lemmas[Section:dev])
    < pf17=(Restore:  0="/dev/null" 1=$feats@_restfxn[Section:dev])
    < pf18=(Restore:  0="/dev/null" 1=$feats@_restpunc[Section:dev])
    < pf19=(Restore:  0="/dev/null" 1=$feats@_restcohverbs[Section:dev])
    < pf20=(CharPrompt: 0="/dev/null" 1=$feats@_charprompt[Section:dev])
    < pf21=(FreqChar: 0="/dev/null" 1=$feats@_freqchar[Section:dev])
    < pf22=(Brown: 0="/dev/null" 1=$feats@_brown[Section:dev])
    > idPreds preds weights pred_meta_path
    :: langs=@
    :: reg=(Regularization: l2_1.0="--l2 1.0" l2_0="--l2 0" l2_0.001="--l2 0.001" l2_0.01="--l2 0.01" l2_0.05="--l2 0.05" l2_0.1="--l2 0.1" l2_0.5="--l2 0.5" l2_5.0="--l2 5.0" l1_1.0="--l1 1.0")
  {
  	# a dummy label ensures that weights are learned for all actual classes (no default/background class)
  	echo "## $langs DUMMY" > header
  
  	set -x
  
   	$creg/dist/bin/creg -y <(cat header $train_gold_lbls) -x $tf1 -x $tf2 -x $tf3 -x $tf4 -x $tf5 -x $tf6 -x $tf7 -x $tf8 -x $tf9 -x $tf10 -x $tf11 -x $tf12 -x $tf13 -x $tf14 -x $tf15 -x $tf16 -x $tf17 -x $tf18 -x $tf19 -x $tf20 -x $tf21 -x $tf22 --z $weights $reg --tx $pf1 --tx $pf2 --tx $pf3 --tx $pf4 --tx $pf5 --tx $pf6 --tx $pf7 --tx $pf8 --tx $pf9 --tx $pf10 --tx $pf11 --tx $pf12 --tx $pf13 --tx $pf14 --tx $pf15 --tx $pf16 --tx $pf17 --tx $pf18 --tx $pf19 --tx $pf20 --tx $pf21 --tx $pf22 > $idPreds
   	
   	cut -f2 $idPreds > $preds
    #cut -f1 $pred_gold_lbls > ids
    #cut -f2 $pred_gold_lbls > golds

    # TODO: implement IO scheme in chunkeval
    # Run evaluation script (requires B- to be prefixed to every label)
    #paste ids <(sed -E 's/^(.+)$/B-\1/' golds) <(sed -E 's/^(.+)$/B-\1/' preds) > idGoldAndPreds
	#$pyutil/chunkeval.py BIO idGoldAndPreds > $results
	
	#echo $pred_tokdir > $pred_tokdir_path
	echo $pred_meta > $pred_meta_path
  }

# Aggregate results over all folds and compute summary statistics
task eval_ : scripts < idPreds=@train_predict weights=@train_predict meta_path=$pred_meta_path@train_predict > results {
	meta=`cat $meta_path`
	set -x
	$scripts/collect_results.py --creg_predictions_files "$idPreds" --metadata_file "$meta" \
		--creg_results_files "$weights" --out_file results
}

# OLD! replaced by train_predict
task trainpredict : creg pyutil 
    < train_gold_lbls=$lbls@extraction[Section:train] train_feats=$feats@extraction[Section:train]
    < pred_gold_lbls=$lbls@extraction[Section:dev] pred_feats=$feats@extraction[Section:dev] 
    < pred_tokdir=$tokdir[Section:dev] pred_meta=$meta[Section:dev]
    	> idPreds preds weights results pred_tokdir_path pred_meta_path
    	:: reg=(Regularization: l2_1.0="--l2 1.0" l2_0="--l2 0" l2_0.001="--l2 0.001" l2_0.01="--l2 0.01" l2_0.05="--l2 0.05" l2_0.1="--l2 0.1" l2_0.5="--l2 0.5" l2_5.0="--l2 5.0" l1_1.0="--l1 1.0") {

    # Now run Creg and store the results in a file.
  	# If it fails, see the stderr.
  	$creg/dist/bin/creg -x $train_feats -y $train_gold_lbls --z $weights $reg --tx $pred_feats > idPreds
    cut -f2 idPreds > preds
    cut -f1 $pred_gold_lbls > ids
    cut -f2 $pred_gold_lbls > golds

    # TODO: implement IO scheme in chunkeval
    # Run evaluation script (requires B- to be prefixed to every label)
    paste ids <(sed -E 's/^(.+)$/B-\1/' golds) <(sed -E 's/^(.+)$/B-\1/' preds) > idGoldAndPreds
	$pyutil/chunkeval.py BIO idGoldAndPreds > $results
	
	echo $pred_tokdir > $pred_tokdir_path
	echo $pred_meta > $pred_meta_path
}

# OLD! replaced by eval_
# Aggregate results over all folds and compute summary statistics
task eval : scripts < idPreds=@trainpredict weights=@trainpredict meta_path=$pred_meta_path@trainpredict > results {
	meta=`cat $meta_path`
	set -x
	$scripts/collect_results.py --creg_predictions_files "$idPreds" --metadata_file "$meta" \
		--creg_results_files "$weights" --out_file results
}

# OLD! TODO: replace with analog of eval_
# Split into 10 partitions (by an arbitrary hash)
task partition : pyutil < data=$out@extraction[Section:train] > partitions :: FOLDS=4 {
	mkdir partitions
	cd partitions
    $pyutil/dataformats/distribute.py --pre-sort md5 -n $FOLDS --batch-start 0 --batch-width 1 $data
}


# OLD! TODO: replace with analog of eval_
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
  	$creg/dist/bin/creg -x train.feats -y train.lbls --z $weights --l1 1.0 --tx test.feats > idPreds
    cut -f2 idPreds > $preds
    cut -f1 test.lbls > ids
    cut -f2 test.lbls > golds
    
    # TODO: implement IO scheme in chunkeval
    # Run evaluation script (requires B- to be prefixed to every label)
    paste ids <(sed -E 's/^(.+)$/B-\1/' golds) <(sed -E 's/^(.+)$/B-\1/' preds) > idGoldAndPreds
	$pyutil/chunkeval.py BIO idGoldAndPreds > $results

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
  of eval_ > DevAcc {
	grep 'Total accuracy:' $results | sed -E 's/\t/ /g' | cut -d' ' -f3 > $DevAcc
  }
  of train_predict > NumFeatures {
  	NLINES=`wc -l $weights | cut -d' ' -f1`
  	echo $((NLINES-1)) > $NumFeatures
  }
}

#task aggregate < results=@xval[Fold:*] {
	# TODO: mean, min, max over all folds? modify collect_results.py?
	# TODO: breakdown by L1?
#	false
#}

plan NewExtract {
	reach _restfxn, _restpunc, _restcohverbs, _charprompt, _freqchar via (Section: train dev)
}

plan DevI1 {
	reach eval_ via (Regularization: l2_0 l2_0.5) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (Coh: 0)
		* (FreqWord: 0) * (PsvRatioFeatures: 1) * (Rep: 0) * (WordRank: 0) * (MisspellFeatures: 1)
	reach eval_ via (Regularization: l2_0 l2_0.5) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (Coh: 0)
		* (FreqWord: 100) * (PsvRatioFeatures: 1) * (Rep: 0) * (WordRank: 0) * (MisspellFeatures: 1)
	reach eval_ via (Regularization: l2_0 l2_0.5) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (Coh: 1) * (CohVerbs: 1)
		* (FreqWord: 0) * (PsvRatioFeatures: 1) * (Rep: 0) * (WordRank: 0) * (MisspellFeatures: 1) * (LemmaFeatures: 0) * (Restore: 0)
	reach eval_ via (Regularization: l2_0 l2_0.5) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (Coh: 0)
		* (FreqWord: 0) * (PsvRatioFeatures: 1) * (Rep: 0) * (WordRank: 0) * (MisspellFeatures: 1) * (LemmaFeatures: 1) * (Restore: 0)
}
plan DevI2 {
	reach eval_ via (Regularization: l2_0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (Coh: 0)
		* (FreqWord: 0) * (PsvRatioFeatures: 1) * (Rep: 0) * (WordRank: 0) * (MisspellFeatures: 1) * (LemmaFeatures: 0) * (Restore: 1)
		
	reach eval_ via (Regularization: l2_0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (Coh: 0)
		* (FreqWord: 0) * (PsvRatioFeatures: 1) * (Rep: 0) * (WordRank: 0) * (MisspellFeatures: 1) * (CharPrompt: 1)
		
	reach eval_ via (Regularization: l2_0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (Coh: 0)
		* (FreqWord: 0) * (PsvRatioFeatures: 1) * (Rep: 0) * (WordRank: 0) * (MisspellFeatures: 1) * (FreqChar: 1)
#	reach eval_ via (Regularization: l2_0) * (PosFeatures: 1) * (PunctFeatures: 1) 
#		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
#		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (Coh: 0)
#		* (FreqWord: 0) * (PsvRatioFeatures: 1) * (Rep: 0) * (WordRank: 0) * (MisspellFeatures: 1) * (PMIFeatures: 1)
}
plan DevI3 {
	reach eval_ via (Regularization: l2_0 l2_1.0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (PsvRatioFeatures: 1) 
		* (MisspellFeatures: 1) * (Brown: 1)
}

#####
# Things below are OLD!
#####

#plan DevAllFeats {
#	reach eval via (Regularization: l2_1.0) * (PosFeatures: 1) * (PunctFeatures: 1) 
#		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) * (Rep: 1) 
#		* (PositionFeatures: 1) * (PsvRatioFeatures: 1) * (WordRank: 1) 
#		* (FreqWord: 1)
#}
plan DevCharsReg {
	reach eval via (Regularization: l2_1.0 l2_0 l2_0.001 l2_0.01 l2_0.05 l2_0.1 l2_0.5 l2_5.0) * (PosFeatures: 0) * (PunctFeatures: 0) 
		* (CxtFxnFeatures: 0) * (CharFeatures: 1) * (PronFeatures: 0) * (Rep: 0) 
		* (PositionFeatures: 0) * (WordRank: 0) 
		* (FreqWord: 0)
		 * (PsvRatioFeatures: 0) # apparently unhelpful
}
plan DevIncremental {
	reach eval via (Regularization: l2_0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (Coh: 1)
		* (FreqWord: 0) * (PsvRatioFeatures: 0) * (Rep: 0) * (WordRank: 0)  # apparently unhelpful
	reach eval via (Regularization: l2_0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (Coh: 0)
		* (FreqWord: 0) * (PsvRatioFeatures: 0) * (Rep: 0) * (WordRank: 0)  # apparently unhelpful
	reach eval via (Regularization: l2_0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 0) * (Coh: 1)
		* (FreqWord: 0) * (PsvRatioFeatures: 0) * (Rep: 0) * (WordRank: 0)  # apparently unhelpful
	reach eval via (Regularization: l2_0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (Coh: 0)
		* (FreqWord: 50) * (PsvRatioFeatures: 0) * (Rep: 0) * (WordRank: 0)
	reach eval via (Regularization: l2_0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (Coh: 0)
		* (FreqWord: 0) * (PsvRatioFeatures: 1) * (Rep: 0) * (WordRank: 0)
	reach eval via (Regularization: l2_0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (Coh: 0)
		* (FreqWord: 0) * (PsvRatioFeatures: 0) * (Rep: 0) * (WordRank: 1)
	reach eval via (Regularization: l2_0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (Coh: 0)
		* (FreqWord: 0) * (PsvRatioFeatures: 1) * (Rep: 0) * (WordRank: 0) * (MisspellFeatures: 1)
	reach eval via (Regularization: l2_0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (Coh: 0)
		* (FreqWord: 0) * (PsvRatioFeatures: 1) * (Rep: 0) * (WordRank: 0) * (MisspellFeatures: 1) * (PMIFeatures: 1)
}
plan DevQuick {
	reach eval via (Regularization: l2_1.0) * (PosFeatures: 0) * (PunctFeatures: 0) 
		* (CxtFxnFeatures: 0) * (CharFeatures: 0) * (PronFeatures: 0) * (Rep: 0) 
		* (PositionFeatures: 0) * (WordRank: 0) * (FreqWord: 0) * (PsvRatioFeatures: 0)
		* (PMIFeatures: 0) * (DocLenFeatures: 0) * (Coh: 1)
}
