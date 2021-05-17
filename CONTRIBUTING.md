
## Clean Master Branch Policy

The watchr-core project follows a **clean master branch policy**.  That is, one should be able to create a stable tag using the master branch at any time.  All unstable/in-progress work goes onto **development branches**.

## Best Practice for Developing as Watchr Team Member

The lead for watchr-core (currently Elliott Ridgway) will create development branches that can be used for core development of new features on Watchr.  To use these branches locally, simply type:
1. git checkout origin/\<the name of the development branch\>
2. git pull origin/\<the name of the development branch\>

## Best Practice for Contributing a Code Change to Watchr

If you are not a watchr-core team member but would like to contribute a code change, you can fork the repo and work out of your own Gitlab project space:

1. From https://gitlab-ex.sandia.gov/SEMS/watchr-core, create a fork of the project by clicking the `Fork` button.

2. Clone your forked copy to your local workstation, replacing:
   - \<forked Git URL\> with the repository URL from the forked repository, e.g., git@gitlab-ex.sandia.gov:\<username\>/jenkins_performance_plugin.git
   - \<my_local_fork\> with a valid name for your local repository.

      ```bash
      git clone <forked Git URL> <my_local_fork>
      ```
3. Add an upstream remote:
      ```bash
      git remote add upstream git@gitlab-ex.sandia.gov:SEMS/jenkins_performance_plugin.git
      ```
4. Create a development branch for yourself.  In Gitlab, click the + button near the top of the main Watchr page and choose "New branch."  Then, in your local repo, type "git checkout --track origin/ \<your development branch\>" and finally "git pull."

## Development workflow

1. In your local repo, checkout a development branch.  It is recommended to name the branch after the code change you're setting out to tackle.
2. Make changes to the necessary source files.
3. Commit your changes to your development branch.
4. Push your development branch to the remote fork you created in [First time setup, step 1](#first-time-setup).

## When should I merge my development branch onto master?

A developer merging from "my_cool_development_branch" back onto master should ask themselves the following three questions:
### 1. Is it tested?
*  Have you written unit tests and gotten code coverage for the code change?
*  If it can't easily be unit-tested (for example, UI code), have you written a test plan so that a non-developer can reproduce steps to black-box test it in the future?
*  Did you run the existing suite of unit tests to verify that nothing broke because of the new code?
### 2. Is it commented / documented?
*  Have you written standard language-specific documentation for any new classes or functions (i.e. Javadoc for Java)?
*  Have you commented portions of the code that are not self-evident?
*  Have you carefully considered whether your code changes will impact public API, and if so, have you bumped version numbers appropriately?  Watchr adheres to [semantic versioning](https://semver.org) for determining when to change major/minor/patch numbers.
### 3. Is it peer-reviewed?
*  Has the code been subjected to a formal code review?  For this project, code reviews are handled via [Gitlab merge requests](#ive-done-all-that-how-do-i-merge).
*  In lieu of a code review, have you presented the new functionality as a demo/presentation to either the customer or to fellow programmers to receive feedback on the feature itself?

## I've done all that.  How do I merge?

Issue a merge request in Gitlab.
* Navigate to your forked repository, and issue a Merge request:  You may see a `New merge request` button at the top of the main window.  Or on the left sidebar, click the `Merge request` link.
* Select your development branch as the source branch and the Watchr master branch as the target branch.
* Submit the merge request. You should then see a `Merge` button to merge your branch to the forked master branch. Optionally, click the box to remove the source branch to delete your feature branch once the merge request is approved.
* Once the merge request is approved, if you clicked the box in the step above, you will see a button to remove the source branch. Click the button to remove your development branch.
* Receive adulation from your fellow teammates.

## Developing for an old revision

Imagine that the software is actively being developed on a specific branch, in preparation for a yet-to-be-released version... for example, version 2.0.  But a customer has found a critical bug in version 1.1, the latest stable release.  A patch for 1.1 needs to be released before 2.0 is ready.  What do you do?
*  Prerequisite:  Follow the [How to develop section](#first-time-setup) for forking and cloning a local repository if you haven't done so already.
*  Create a branch in your local repository using the stable release tag (1.1 in our example):
```
git checkout -b <name of your new branch> <revision number to start from>
```
*  Working on the new branch, fix the bug and test according to [the above-described requirements](#is-it-tested) for testing rigor.
*  A new release of the software should be tagged using the branch.  See [the tagging section](#releasing-a-tag).
*  The chosen version number of the patch release should follow [semantic versioning](https://semver.org).
*  After tagging, issue a merge request to merge your changes onto the main development branch (NOT master, per the Clean Master Branch policy) so that your branch work can be included in the next planned release.

## Releasing a Tag

### Option 1:  From the master branch

Simply use Gitlab's "New tag" feature.  (Obviously, make sure all code in your local repo has been pushed to the remote master branch)

### Option 2:  From your local repo (i.e. tagging from a non-master branch)
```
git tag -a v1.4 -m "my version 1.4"
```
Note:  “-a” is the annotated tags option:
>  "Annotated tags, however, are stored as full objects in the Git database. They’re checksummed; contain the tagger name, email, and date; have a tagging message; and can be signed and verified with GNU Privacy Guard (GPG). It’s generally recommended that you create annotated tags so you can have all this information; but if you want a temporary tag or for some reason don’t want to keep the other information, lightweight tags are available too."
```
git push origin --tags
```
