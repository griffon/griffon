if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  echo -e "Starting gh-pages update\n"

  #copy data were interested in to other place
  for projectDir in subprojects plugins samples
  do
    for project in `ls $TRAVIS_BUILD_DIR/projectDir`
    do
      rm -rf $HOME/travis-ci/reports/$project
      mkdir -p $HOME/travis-ci/reports/$project
      if [ -d "$TRAVIS_BUILD_DIR/$projectDir/$project/build/reports/" ]; then
          echo -e "Copying $TRAVIS_BUILD_DIR/$projectDir/$project/build/reports"
          cp -R "$TRAVIS_BUILD_DIR/$projectDir/$project/build/reports/" "$HOME/travis-ci/reports/$project"
      fi
    done
  done

  #go to home and setup git
  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis"

  #using token clone gh-pages branch
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/aalmiray/griffon2.git gh-pages > /dev/null

  #go into directory and copy data we're interested in to that directory
  cd gh-pages/travis-ci
  cp -Rf $HOME/travis-ci/* .

  #add, commit and push files
  git add -f .
  git commit -m "Travis build $TRAVIS_BUILD_NUMBER pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo -e "Done saving test reports\n"
fi
