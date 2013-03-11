global {
	ducttape_experimental_imports=true
	ducttape_experimental_packages=true
	datadir=../NLI_2013_Training_Data
}

package scripts :: .versioner=disk .path=/home/nschneid/l1id/system/src {}

package pyutil :: .versioner=git .repo="git://github.com/nschneid/pyutil.git" .ref=HEAD { }

package pos_tagger :: .versioner=disk .path=/home/nschneid/stanford-postagger-full-2012-11-11 {}

package creg :: .versioner=git .repo="git://github.com/redpony/creg.git" .ref=HEAD {
	module load clab-gcc
	module load clab-boost
	./bjam
}
