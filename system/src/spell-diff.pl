#!/usr/bin/perl -w
use strict;

die "Usage: $0 file1.raw file1.corrected\n" unless scalar @ARGV == 2;

my $raw = $ARGV[0];
my $cor = $ARGV[1];
open R, "<$raw" or die "Can't read $raw: $!";
open C, "<$cor" or die "Can't read $raw: $!";
open RO, ">raw.$$" or die;
open RC, ">cor.$$" or die;
while(<R>) {
  chomp;
  next if /^\s*$/;
  $_ = lc $_;
  my @words = split / /;
  for my $w (@words) {
    print RO "$w\n";
  }
}
while(<C>) {
  chomp;
  next if /^\s*$/;
  $_ = lc $_;
  my @words = split / /;
  for my $w (@words) {
    print RC "$w\n";
  }
}
close RO;
close RC;
my $o = `diff -y raw.$$ cor.$$ | egrep '\\||>|<' | egrep '[a-z][a-z][a-z]'`;
my @lines = split /\n/, $o;
unlink "raw.$$";
unlink "cor.$$";

my @types;
my @as;
my @bs;
for my $line (@lines) {
  if ($line =~ /(.*?)\s+([<|>])\s+([^ ]+)$/) {
    my $a = $1;
    my $type = $2;
    my $b = $3;
    push @as, $a;
    push @bs, $b;
    push @types, $type;
  }
}

my $n = scalar @types;
my %v;
for (my $i = 0; $i < $n; $i++) {
  unless ($as[$i] =~ /^(night|eight|would|could|well|hell|should)$/) {
    $v{"MSP:$bs[$i]"}++;
    if ($types[$i] eq '|') {
      if ($i == $n-1 || $types[$i+1] eq '|') {
        extract_feats($as[$i], $bs[$i], \%v);
      } elsif ($i < $n-1) {
        $v{"SEP:$bs[$i]:$bs[$i+1]"}++;
        $v{"SEP:$bs[$i]"}++;
        $v{"SEP"}++;
      }
    }
  }
}

for my $k (keys %v) {
  my $val = log($v{$k} + 1);
  print "$k\t$val\n";
}

sub extract_feats {
  my ($a, $b, $r) = @_;
  open A, ">a.$$" or die;
  open B, ">b.$$" or die;
  my @as = split //, $a;
  my @bs = split //, $b;
  for my $aa (@as) { print A "$aa\n"; }
  close A;
  for my $bb (@bs) { print B "$bb\n"; }
  close B;
  my $o = `diff -y a.$$ b.$$`;
  unlink "a.$$";
  unlink "b.$$";
  my @lines = split /\n/, $o;
  my $len = scalar @lines;
  my $cur = 0;
  my $prev = '#';
  for my $line (@lines) {
    $cur++;
    my $is_first = ($cur == 1);
    my $is_last = ($cur == $len);
    my $pos = 'MID';
    if ($is_first) { $pos = 'FIRST'; } elsif ($is_last) { $pos = 'LAST'; }
    # print "$line\n";
    if ($line =~ /(.*?)\s*([<|>])\s*([^ ]*)$/) {
      my $a = $1;
      my $type = $2;
      my $b = $3;
      if ($type eq '<') {
        $r->{"DEL:$a"}++;
        $r->{"$pos:DEL:$a"}++;
        if ($a eq $prev) {
          $r->{"FIX_DOUBLE:$a"}++;
          $r->{"$pos:FIX_DOUBLE:$a"}++;
        }
      } elsif ($type eq '|') {
        $r->{"SUBST:$a:$b"}++;
        $r->{"$pos:SUBST:$a:$b"}++;
      } else {
        $r->{"INS:$b"}++;
        $r->{"$pos:INS:$b"}++;
        if ($b eq $prev) { $r->{"DOUBLE:$b"}++; }
      }
      $prev = $a;
    } else {
      $prev = substr($line,0,1);
    }
  }
}
