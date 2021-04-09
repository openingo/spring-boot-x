#!/bin/bash
echo "======START PULL========"
# shellcheck disable=SC2045
# shellcheck disable=SC2006
for file in `ls`;
do
if [ -d "$file" ]
then 
  # shellcheck disable=SC2046
  # shellcheck disable=SC2164
  # shellcheck disable=SC2006
  # shellcheck disable=SC2116
  cd `echo "$file"`
  # shellcheck disable=SC2006
  current_branch=`git symbolic-ref --short -q HEAD`
  echo "→ → → processing【 $file 】at $current_branch"
  echo "→ → → pulling from $current_branch"
  git pull origin "${current_branch}"
  # shellcheck disable=SC2028
  echo "\n"
  cd ..
fi
done
echo "======PULL OVER========"