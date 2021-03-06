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

(ns bob.execution.internals
  (:require [failjure.core :as f]
            [bob.util :refer [unsafe! format-id]])
  (:import (com.spotify.docker.client DefaultDockerClient DockerClient$LogsParam
                                      DockerClient$ListImagesParam LogStream)
           (com.spotify.docker.client.messages HostConfig ContainerConfig ContainerCreation
                                               ContainerState ContainerInfo)
           (java.util List)))

(def default-image "debian:unstable-slim")

(defonce docker
         (.build (DefaultDockerClient/fromEnv)))

(def host-config ^HostConfig (.build (HostConfig/builder)))

(def log-params (into-array DockerClient$LogsParam
                            [(DockerClient$LogsParam/stdout)
                             (DockerClient$LogsParam/stderr)]))

(defn- has-image
  "Checks if an image is present locally.
  Returns the name or the error if any."
  [name]
  (let [result (unsafe! (.listImages ^DefaultDockerClient docker
                                     (into-array DockerClient$ListImagesParam
                                                 [(DockerClient$ListImagesParam/byName name)])))]
    (if (or (f/failed? result) (zero? (count result)))
      (f/fail "Failed to find %s" name)
      name)))

(defn kill-container
  "Kills a running container using SIGKILL.
  Returns the name or the error if any."
  [name]
  (if (f/failed? (unsafe! (.killContainer ^DefaultDockerClient docker name)))
    (f/fail "Could not kill %s" name)
    name))

(defn pull
  "Pulls in an image if it's not present locally.
  Returns the name or the error if any."
  [name]
  (if (and (f/failed? (has-image name))
           (f/failed? (unsafe! (do (println (format "Pulling %s" name))
                                   (.pull ^DefaultDockerClient docker name)
                                   (println (format "Pulled %s" name))))))
    (f/fail "Cannot pull %s" name)
    name))

(defn config-of
  "Creates a container config of a container before the build.
  Returns the config object or the error if any."
  [^String image ^List cmd ^List evars]
  (unsafe! (-> (ContainerConfig/builder)
               (.hostConfig host-config)
               (.env evars)
               (.image image)
               (.cmd cmd)
               (.build))))

(defn build
  "Builds a container.
  Takes the base image and the entry point command.
  Returns the id of the built container."
  [^String image ^List cmd ^List evars]
  (unsafe! (let [config   ^ContainerConfig (config-of image cmd evars)
                 creation ^ContainerCreation (.createContainer ^DefaultDockerClient docker config)]
             (format-id (.id creation)))))

(defn status-of
  "Returns the status of a container by id."
  [^String id]
  (let [result (unsafe! (.inspectContainer ^DefaultDockerClient docker id))]
    (if (f/failed? result)
      (f/message result)
      (let [state ^ContainerState (.state ^ContainerInfo result)]
        {:running  (.running state)
         :exitCode (.exitCode state)}))))

(defn run
  "Synchronously starts up a previously built container.
  Returns the id when complete or and error in case on non-zero exit."
  [^String id]
  (f/attempt-all [_      (unsafe! (.startContainer ^DefaultDockerClient docker id))
                  _      (unsafe! (.waitContainer ^DefaultDockerClient docker id))
                  status (status-of id)]
    (if (zero? (:exitCode status))
      (format-id id)
      (f/fail "Abnormal exit."))
    (f/when-failed [err] err)))

(defn log-stream-of
  "Fetches the lazy log stream from a running/dead container."
  ^LogStream [^String name]
  (unsafe! (.logs ^DefaultDockerClient docker name log-params)))
