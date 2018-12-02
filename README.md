# Parkrun-app

[![Built with Spacemacs](https://cdn.rawgit.com/syl20bnr/spacemacs/442d025779da2f62fc86c2082703697714db6514/assets/spacemacs-badge.svg)](http://spacemacs.org)

A (soon to be) [parkrun](https://www.parkrun.com/) notification app, written in Clojure.

Scrapes data from [parkrun cancellations](https://www.parkrun.com/cancellations/) page at given time (1 second from `now` for the conference), filters dates to get the next weekend, searches for a particular parkrun of interest and prints out cancellation message if such parkrun is cancelled. 

## TODO - future improvements - next version
* Fix the job to run on Friday afternoon :tada:
* Send actual emails/messages when such parkrun is cancelled (using AWS and [Amazonica](https://github.com/mcohen01/amazonica) for SNS) :email: :iphone:
* Make a fancy frontend :nail_care:
* Suggest other parkrun nearby if the one of interest is cancelled :world_map:
* Possibilities are endless :boom:

## Usage

LightTable - open `core.clj` and press `Ctrl+Shift+Enter` to evaluate the file.

Emacs - run cider, open `core.clj` and press `C-c C-k` to evaluate the file.

REPL - run `(require 'parkrun-app.core)`.

## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
