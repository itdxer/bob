;   This file is part of Bob.
;
;   Bob is free software: you can redistribute it and/or modify
;   it under the terms of the GNU General Public License as published by
;   the Free Software Foundation, either version 3 of the License, or
;   (at your option) any later version.
;
;   Bob is distributed in the hope that it will be useful,
;   but WITHOUT ANY WARRANTY; without even the implied warranty of
;   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
;   GNU General Public License for more details.
;
;   You should have received a copy of the GNU General Public License
;   along with Bob. If not, see <http://www.gnu.org/licenses/>.

(ns bob.api.schemas
  (:require [schema.core :as s])
  (:import (clojure.lang Keyword)))

(s/defschema SimpleResponse {:message String})

(s/defschema Pipeline {:steps [String]
                       :image String
                       :vars  [{Keyword String}]})

(s/defschema LogsResponse {:message [String]})

(s/defschema StatusResponse {:message (s/enum :running :passed :failed)})
