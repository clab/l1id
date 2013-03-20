# ducttape workflow by nschneid@cs.cmu.edu

import config.tape


global {
	# "Section: dev" really means held-out
	tokdir=(Section: train="../system/data/input/NLI_2013_Training_Data/tokenized" dev="../system/data/input/NLI_2013_Development_Data/tokenized" test="../system/data/input/NLI_2013_Test_Data/tokenized")
	
	posdir=(Section: train="../system/data/work/pos_tagged_train" dev="../system/data/work/pos_tagged_dev" test="../system/data/work/pos_tagged_test")
	
	  meta=(Section: train="../system/data/input/NLI_2013_Training_Data/index-training.csv" dev="../system/data/input/NLI_2013_Development_Data/index-dev.csv" test="../system/data/input/NLI_2013_Test_Data/index-test.csv")
	
	 langs="ARA CHI FRE GER HIN ITA JPN KOR SPA TEL TUR"
}


func extract_instances : scripts < tokdir posdir meta > lbl feats :: flags {
	$scripts/extract_instances.py --input_dir="$tokdir" --metadata_filename="$meta" \
  --features_filename=$feats --labels_filename=$lbls $flags --pos_tagged_dir_ngrams="$posdir" --pos_tagged_dir_repetitions=$posdir --pos_tagged_dir_passive_verbs="$posdir" --pos_tagged_dir_lemmas="$posdir" --misspelling_extractor="$scripts/spell-diff.pl" --more_frequent_characters_ngrams_input_dir="$tokdir"
}

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
	< bcounts1=../system/data/input/NLI_2013_Training_Data/all_brown.1grams
	< bcounts2=../system/data/input/NLI_2013_Training_Data/all_brown.2grams
	< bcounts3=../system/data/input/NLI_2013_Training_Data/all_brown.3grams
	< bcounts4=../system/data/input/NLI_2013_Training_Data/all_brown.4grams
         > feats {
    
    bcountsdir=`dirname $bcounts1`     
    
    $scripts/extract_instances.py --input_dir="$tokdir" --metadata_filename="$meta" \
  --features_filename=$feats --labels_filename=lbls --append_brown_ngrams=True --max_brown_ngrams_order=4 --max_brown_prefix_length=16 --brown_freq_filter=15 --brown_clusters_filename=$brown_clusters --corrected_ngram_counts_prefix="$bcountsdir/all_brown."
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
    < warm=models/l2_10_T_10_i_180
    > idPreds preds weights pred_meta_path
    :: langs=@
    :: reg=(Regularization: l2_1.0="--l2 1.0" l2_0="--l2 0" l2_0.001="--l2 0.001" l2_0.01="--l2 0.01" l2_0.05="--l2 0.05" l2_0.1="--l2 0.1" l2_0.5="--l2 0.5" l2_2.0="--l2 2.0" l2_5.0="--l2 5.0" l2_10.0="--l2 10.0" l2_10_T_10="--l2 10.0 -T 10.0" l1_1.0="--l1 1.0" l1_10.0="--l1 10.0")
  {
  	# a dummy label ensures that weights are learned for all actual classes (no default/background class)
  	echo "## $langs DUMMY" > header
  
  	set -x
  
   	$creg/dist/bin/creg -w $warm -y <(cat header $train_gold_lbls) -x $tf1 -x $tf2 -x $tf3 -x $tf4 -x $tf5 -x $tf6 -x $tf7 -x $tf8 -x $tf9 -x $tf10 -x $tf11 -x $tf12 -x $tf13 -x $tf14 -x $tf15 -x $tf16 -x $tf17 -x $tf18 -x $tf19 -x $tf20 -x $tf21 -x $tf22 --z $weights $reg --tx $pf1 --tx $pf2 --tx $pf3 --tx $pf4 --tx $pf5 --tx $pf6 --tx $pf7 --tx $pf8 --tx $pf9 --tx $pf10 --tx $pf11 --tx $pf12 --tx $pf13 --tx $pf14 --tx $pf15 --tx $pf16 --tx $pf17 --tx $pf18 --tx $pf19 --tx $pf20 --tx $pf21 --tx $pf22 > $idPreds
   	
   	cut -f2 $idPreds > $preds
    
	echo $pred_meta > $pred_meta_path
  }

task predict_ : creg pyutil
	< weights=@train_predict
    < pred_meta=$meta[Section:test]
    < pf1=(PunctFeatures:   0="/dev/null" 1=$feats@_punct[Section:test])
    < pf2=(PosFeatures:     0="/dev/null" 1=$feats@_pos[Section:test])
    < pf3=(CharFeatures:    0="/dev/null" 1=$feats@_char[Section:test])
    < pf4=(CxtFxnFeatures:  0="/dev/null" 1=$feats@_cxtfxn[Section:test])
    < pf5=(PronFeatures:    0="/dev/null" 1=$feats@_pron[Section:test])
    < pf6=(Rep:     0="/dev/null" 1=$feats@_rep[Section:test])
    < pf7=(PositionFeatures:  0="/dev/null" 1=$feats@_position[Section:test])
    < pf8=(PsvRatioFeatures:  0="/dev/null" 1=$feats@_psvratio[Section:test])
    < pf9=(WordRank:  0="/dev/null" 1=$feats@_wordrank[Section:test])
    < pf10=(FreqWord: 0="/dev/null" 100=$feats@_freqword[Section:test]) 
    < pf11=(PMIFeatures:      0="/dev/null" 1=$feats@_pmi[Section:test])
    < pf12=(DocLenFeatures:   0="/dev/null" 1=$feats@_doclen[Section:test])
    < pf13=(Coh: 0="/dev/null" 1=$feats@_cohesive[Section:test])
    < pf14=(MisspellFeatures: 0="/dev/null" 1=$feats@_misspell[Section:test])
    < pf15=(CohVerbs: 0="/dev/null" 1=$feats@_cohverbs[Section:test])
    < pf16=(LemmaFeatures:  0="/dev/null" 1=$feats@_lemmas[Section:test])
    < pf17=(Restore:  0="/dev/null" 1=$feats@_restfxn[Section:test])
    < pf18=(Restore:  0="/dev/null" 1=$feats@_restpunc[Section:test])
    < pf19=(Restore:  0="/dev/null" 1=$feats@_restcohverbs[Section:test])
    < pf20=(CharPrompt: 0="/dev/null" 1=$feats@_charprompt[Section:test])
    < pf21=(FreqChar: 0="/dev/null" 1=$feats@_freqchar[Section:test])
    < pf22=(Brown: 0="/dev/null" 1=$feats@_brown[Section:test])
    > idPreds preds pred_meta_path csv="cmu-closed-1.csv"
  {
  	
  	set -x
  
   	$creg/dist/bin/creg -w $weights -W --tx $pf1 --tx $pf2 --tx $pf3 --tx $pf4 --tx $pf5 --tx $pf6 --tx $pf7 --tx $pf8 --tx $pf9 --tx $pf10 --tx $pf11 --tx $pf12 --tx $pf13 --tx $pf14 --tx $pf15 --tx $pf16 --tx $pf17 --tx $pf18 --tx $pf19 --tx $pf20 --tx $pf21 --tx $pf22 > $idPreds
   	
   	cut -f2 $idPreds > $preds
   	
   	sed -e 's/\t/,/' $idPreds | sed -e 's/^..._//' > $csv
   	
	echo $pred_meta > $pred_meta_path
  }

# Aggregate results over all folds and compute summary statistics
task eval_ : scripts < idPreds=@train_predict weights=@train_predict meta_path=$pred_meta_path@train_predict > results {
	meta=`cat $meta_path`
	set -x
	$scripts/collect_results.py --creg_predictions_files "$idPreds" --metadata_file "$meta" \
		--creg_results_files "$weights" --out_file results
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
	reach eval_ via (Regularization: l2_0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 1) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (PsvRatioFeatures: 1) 
		* (MisspellFeatures: 1) * (Brown: 1)
	
	reach eval_ via (Regularization: l2_0 l2_1.0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 0) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (PsvRatioFeatures: 1) 
		* (MisspellFeatures: 1) * (FreqChar: 1)
}
plan DevI4 {
	reach eval_ via (Regularization: l2_1.0 l2_2.0 l2_5.0 l2_10.0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 0) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (PsvRatioFeatures: 1) 
		* (MisspellFeatures: 1) * (Brown: 0) * (FreqChar: 1) * (CharPrompt: 1) * (Restore: 1)
}
plan DevI5 {
	reach eval_ via (Regularization: l2_5.0 l2_10.0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 0) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (PsvRatioFeatures: 1) 
		* (MisspellFeatures: 1) * (Brown: 1) * (FreqChar: 1) * (CharPrompt: 1) * (Restore: 1)
}
plan DevI6 {
	reach eval_ via (Regularization: l2_10_T_10 l1_10.0) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 0) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (PsvRatioFeatures: 1) 
		* (MisspellFeatures: 1) * (Brown: 1) * (FreqChar: 1) * (CharPrompt: 1) * (Restore: 1)
}
plan TestExtract {
	reach _punct,_pos,_cxtfxn,_pron,_position,_psvratio,_freqword,_doclen,_misspell,_restfxn,_restpunc,_restcohverbs,_charprompt,_freqchar,_brown via (Section: test)
	# omitting: _pmi,_rep,_cohesive,_cohverbs,_lemmas,_char,_wordrank
}
plan TestPredict {
	reach predict_	via	(Regularization: l2_10_T_10) * (PosFeatures: 1) * (PunctFeatures: 1) 
		* (CxtFxnFeatures: 1) * (CharFeatures: 0) * (PronFeatures: 1) 
		* (PositionFeatures: 1) * (DocLenFeatures: 1) * (PsvRatioFeatures: 1) 
		* (MisspellFeatures: 1) * (Brown: 1) * (FreqChar: 1) * (CharPrompt: 1) * (Restore: 1)
}
