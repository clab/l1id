#!/usr/bin/perl -w
use strict;

die "Usage: $0 STRING FILE\n" unless scalar @ARGV == 2;

my $line = shift @ARGV;
open F, "<$ARGV[0]" or die "Can't read $ARGV[0]: $!\n";
my @lines = <F>;
close F;

open F, ">$ARGV[0]" or die "Can't write $ARGV[0]: $!\n";
print F "$line\n";
for my $l (@lines) {
  print F $l;
}
close F;

