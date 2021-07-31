#!/bin/bash

repl(){
  # lein repl :headless :host 0.0.0.0 :port 35543
  lein repl :start :host 0.0.0.0 :port 35543
}

jess(){
    # java -classpath /opt/jess.jar jess.Main
    java -classpath /opt/Jess61p4/jess.jar jess.Main "$@"
}

jess_os(){
    # java -classpath /opt/jess.jar jess.Main
    java -classpath jess.jar jess.Main "$@"
}

download_jess(){
    curl -O https://manning-content.s3.amazonaws.com/download/e/839f142-236f-4866-8693-abf06add86de/friedman-hill_src_1_jess_se.zip && \
    unzip friedman-hill_src_1_jess_se.zip && \
    cp Jess61p4/jess.jar jess.jar
}


get_bert_data(){
  data_path=data

  # if [ ! -d "$data_path" ]; then
    mkdir -p "$data_path"
    curl https://s3.us-east-2.amazonaws.com/mxnet-scala/scala-example-ci/BertQA/vocab.json -o $data_path/vocab.json
    curl https://s3.us-east-2.amazonaws.com/mxnet-scala/scala-example-ci/BertQA/static_bert_qa-0002.params -o $data_path/static_bert_qa-0002.params
    curl https://s3.us-east-2.amazonaws.com/mxnet-scala/scala-example-ci/BertQA/static_bert_qa-symbol.json -o $data_path/static_bert_qa-symbol.json
  # fi
}

get_cnn_data(){
  mkdir -p data/mr-data
  cd data/mr-data
  wget https://raw.githubusercontent.com/yoonkim/CNN_sentence/master/rt-polarity.neg
  wget https://raw.githubusercontent.com/yoonkim/CNN_sentence/master/rt-polarity.pos
  cd ../..
  mkdir -p data/glove
  cd data/glove
  wget http://nlp.stanford.edu/data/glove.6B.zip
  unzip *.zip
  cd ../..
}

get_rnn_data(){
  mkdir -p data
  cd data
  wget http://data.mxnet.io/mxnet/data/char_lstm.zip
  unzip char_lstm.zip
  cd ..
}

get_mnist_data(){

  if [ ! -z "$MXNET_HOME" ]; then
    data_path="$MXNET_HOME"
  else
    data_path="./data"
  fi

  if [ ! -d "$data_path" ]; then
    mkdir -p "$data_path"
  fi

  mnist_data_path="$data_path/mnist.zip"
  if [ ! -f "$mnist_data_path" ]; then
    wget http://data.mxnet.io/mxnet/data/mnist.zip -P $data_path
    cd $data_path
    unzip -u mnist.zip
  fi
}

install_mxnet(){
  VERSION=1.5.0-SNAPSHOT

  git clone --recursive https://github.com/apache/incubator-mxnet.git ~/mxnet
  cd ~/mxnet
  # git checkout tags/${VERSION} -b my_mxnet
  git checkout 372f53100be13b04ca84523dc60deb69ba21e6ac
  git submodule update --init --recursive
  cd contrib/clojure-package

  pwd
  ls -a

  FILE=project.clj
  # ORIGINAL="\"<insert-snapshot-version>\""
  # NEW="\"${VERSION}\""

  # get the right scala jar version
  ORIGINAL=";\[org.apache.mxnet/mxnet-full_2.11-linux-x86_64-cpu \"<insert-snapshot-version>\""
  NEW="\[org.apache.mxnet/mxnet-full_2.11-linux-x86_64-cpu \"${VERSION}\""
  sed -i "s~${ORIGINAL}~${NEW}~g" $FILE

  # remove INTRNAL scala jar dep
  ORIGINAL="\[org.apache.mxnet/mxnet-full_2.11 "
  NEW=";\[org.apache.mxnet/mxnet-full_2.11 "
  sed -i "s~${ORIGINAL}~${NEW}~g" $FILE

  # lein test
  lein install
}

get_mnist(){
  #https://stackoverflow.com/questions/8880603/loop-through-an-array-of-strings-in-bash
  declare -a names=("train-images-idx3-ubyte" "t10k-images-idx3-ubyte" "t10k-labels-idx1-ubyte"  "train-labels-idx1-ubyte")

  BASE_URI=http://yann.lecun.com/exdb/mnist

  echo $BASE_URI

  for name in "${names[@]}"
  do
      echo $name
      curl -o ./data/${name}.gz ${BASE_URI}/${name}.gz 
      gzip -d ./data/${name} 
      # tar -C ./data/${name} -xzvf ./data/${name}.gz
  done
}

get_sdss(){
  URI=https://www.kaggle.com/lucidlenn/sloan-digital-sky-survey/downloads/sloan-digital-sky-survey.zip/2
  # required authentication ((
  
}


  # tar -C ./data/train-images-idx3-ubyte -xzvf ./data/train-images-idx3-ubyte.gz
  # v verbose
  # z - unzip
  # x extract
  # file


"$@"
