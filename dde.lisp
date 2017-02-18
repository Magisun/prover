(load "/Users/naveensundarg/projects/prover/snark-20120808r02/snark-interface.lisp")

(in-package :snark-user)

(defun declare-ec-sort-system ()
  (declare-sort 'Fluent)
  (declare-sort 'Event)
  (declare-subsort 'Action 'Event)
  
  (declare-relation 'Initiates 3 :sort '(Event Fluent Number))
  (declare-relation 'Terminates 3 :sort '(Event Fluent Number))
  (declare-relation 'Releases 3 :sort '(Event Fluent Number))

  (declare-relation 'InitiallyP 1 :sort '(Fluent))
  (declare-relation 'InitiallyN 1 :sort '(Fluent))
 
  (declare-relation 'Happens 2 :sort '(Event Number))
  (declare-relation 'Happens 3 :sort '(Event Number Number))
  (declare-relation 'HoldsAt 2 :sort '(Fluent Number))
  
  (declare-relation 'Clipped 3 :sort '(Number Fluent Number))
  (declare-relation 'DeClipped 3 :sort '(Number Fluent Number))

  (declare-relation 'Trajectory 4 :sort '(Fluent Number Fluent Number))
  
  (declare-relation 'Prior 2 :sort '(Number Number))
  
  (declare-constant 't0 :sort 'Number)
  (declare-constant 't1 :sort 'Number)
  (declare-constant 't2 :sort 'Number)
  (declare-constant 't3 :sort 'Number)
  (declare-constant 't4 :sort 'Number)
  (declare-constant 'unit :sort 'Number))




(defparameter *SC1* '(forall ((?f Fluent) (?t Number))
		      (implies (and (InitiallyP ?f) (not (Clipped t0 ?f ?t)))
		       (HoldsAt ?f ?t))))

(defparameter *SC2* '(forall ((?t1 Number) (?t2 Number) (?e Event) (?f Fluent))
			    (implies (and (Happens ?e ?t1)
					  (Initiates ?e ?f ?t1)
					  (Prior ?t1 ?t2)
					  (not (Clipped ?t1 ?f ?t2)))
				     (HoldsAt ?f ?t2))))


(defparameter *SC3* '(forall ((?t1 Number) (?f Fluent) (?t2 Number))
		      (iff (Clipped ?t1 ?f ?t2 )
		       (exists ((?e Event) (?t Number))
			(and (Happens ?e ?t)
			     (Prior ?t1 ?t)
			     (Prior ?t ?t2)
			     (Terminates ?e ?f ?t))))))



(defparameter *XC9*
  '(forall ((?e Event) (?f Fluent) (?g Fluent) (?t1 Number) (?t2 Number) (?delta Number))
    (implies (and
	      (Happens ?e ?t1)
	      (Initiates ?e ?f ?t1)
	      (not (Clipped ?t1 ?f ?t2))
	      (Trajectory ?f ?t1 ?g ?delta)
	      (= ?t2 (+ ?t1 ?delta)))
     (HoldsAt ?g ?t2))))


(defparameter *EC-Axioms* (list *SC1* *SC2* *SC3* *XC9*))



(defun declare-dde-commons ()
  (declare-ec-sort-system)
  (snark:declare-sort 'Track)
  (snark:declare-sort 'Moveable :subsorts-incompatible t)
  (snark:declare-subsort 'Person 'Moveable)
  (snark:declare-subsort 'Train  'Moveable)

  (snark:declare-constant 'P1 :sort 'Person)
  (snark:declare-constant 'P2 :sort 'Person)
  (snark:declare-constant 'P3 :sort 'Person)
  
  (snark:declare-constant 'train :sort 'Train)
  (snark:declare-constant 'track1 :sort 'Track)
  (snark:declare-constant 'track2 :sort 'Track)
  (snark:declare-function '+ 2 :sort '(Number Number Number))

  (snark:declare-function 'position 3 :sort '(Fluent Moveable Track Number))

  (snark:declare-function 'switch 2 :sort '(Event Track Track))
  (snark:declare-relation 'SwitchPoint 2 :sort '(Track Number))
  (snark:declare-function 'dead 1 :sort '(Fluent Person))
  (snark:declare-function 'onrails 2 :sort '(Fluent Train Track))
  (snark:declare-constant 'start :sort 'Event)
  (snark:declare-constant 'motion :sort 'Fluent))


(defun +-REWRITER (term subst)
    (let ((c1 (first (args term)))
	  (c2 (second (args term))))
      (dereference c1 subst
                   :if-constant
                   (dereference c2 subst
				:if-constant
				(+ c1 c2)
				:if-variable none
				:if-compound none)
                   :if-variable none
                   :if-compound none)))


(defun assert-add-table (end)
  (loop for i from 0 to end do
       (loop for j from 0 to end do
	    (if (< i j)
		(assert `(Prior ,i ,j)))
	    (if (< j i)
		(assert `(Prior ,j ,i)))
	    (assert `(= ,(+ i j) (+ ,i ,j)))
	    (assert `(= ,(+ i j) (+ ,j ,i ))))))

(defun assert-domain (end)
  (assert `(forall ((?p Number))
	    (implies (Prior ?p ,end)
	     ,(cons 'or (loop for i from 0 to end collect 
		       `(= ,i ?p)))))))

;  (declare-function '+ 2 :rewrite-code '+-REWRITER)

(defparameter *horizon* 6)
(defparameter *arithmetic-max* 10)

(defun assert-common-dde-axioms! ()
  (mapcar #'assert *EC-Axioms*)
  
  (assert-add-table *arithmetic-max*)
  (assert-domain *horizon*)
  
  ;; Basic Trajectory Axiom.
  (assert '(forall ((?train Train) (?track Track) (?s Number) (?t Number))
	    (Trajectory	     
	     (onrails ?train ?track)
	     ?s
	     (position ?train ?track ?t)
	     ?t)))
  
  ;; On a single track, any moveable can be in just one position.
  (assert '(forall ((?p1 Number) (?p2 Number) (?m Moveable) (?t Track))
	    (implies (and (position ?m ?t ?p1) (position ?m ?t ?2))
	     (= ?p1 ?p2))))
  
  ;; Starting the simulation places the train on track1.
  (assert '(forall ((?t Number)) (Initiates start (onrails train track1) ?t))) 
  
  ;; We start the simulation at time 0. 
  (assert '(Happens start 0))
  
  
  ;; Switching from track 1 to track 2, initiates onrails for track 2.
  (assert '(forall ((?t Number) (?track1 Track) (?track2 Track))
	    (Initiates (switch ?track1 ?track2) (onrails train ?track2) ?t)))
  
  ;; Switching from track 1 to track 2, terminates onrails for track 1.
  (assert '(forall ((?t Number) (?track1 Track) (?track2 Track))
	    (Terminates (switch ?track1 ?track2) (onrails train ?track1) ?t)))
  
  ;; if for any track, train, person, time and position, the train and person are at the same position
  ;; then the person is dead at the next time step. 
  (assert '(forall ((?track Track) (?train Train) (?person Person) (?time Number) (?pos Number))
	    (implies (and 
		  (HoldsAt (position ?train ?track ?pos) ?time)
		  (HoldsAt (position ?person ?track ?pos) ?time))
	     (HoldsAt (dead ?person) (+ 1 ?time)))))
  
  ;;initial conditions
  ;; At time 0, no trains are present on any track. 
  (assert '(forall ((?p Number) (?train Train) (?track Track)) 
	    (not (HoldsAt (position ?train ?track ?p) 0))))
  
  ;; Condition for P1
  (assert `(implies 
	    (forall ((?t Number))
		    (implies (Prior ?t ,*horizon*) (not (HoldsAt (position train track1 4) ?t))))
	    (forall ((?t Number))
		    (implies (Prior ?t ,*horizon*) (not (HoldsAt (dead P1) ?t))))))

   ;; Condition for P2
  (assert `(implies 
	    (forall ((?t Number))
		    (implies (Prior ?t ,*horizon*) (not (HoldsAt (position train track1 5) ?t))))
	    (forall ((?t Number))
		    (implies (Prior ?t ,*horizon*) (not (HoldsAt (dead P2) ?t))))))

  ;; Condition for P3
  (assert `(implies 
	    (forall ((?t Number))
		    (implies (Prior ?t ,*horizon*) (not (HoldsAt (position train track2 3) ?t))))
	    (forall ((?t Number))
		    (implies (Prior ?t ,*horizon*) (not (HoldsAt (dead P3) ?t))))))

  (assert '(forall ((?train Train) (?person Person) (?track Track) (?pos Number))
  	    (implies
  	     (not 
  	      (exists ((?t Number)) 
  		      (and (HoldsAt (position ?train ?track ?pos) ?t)
  			   (HoldsAt (position ?person ?track ?pos) ?t))))
  	     (not (exists ((?t Number)) (Holds (dead ?person) ?t))))))
 
  (assert '(forall ((?t Number)) (HoldsAt (position P1 track1 4) ?t)))
  (assert '(forall ((?t Number)) (HoldsAt (position P2 track1 5) ?t)))
  (assert '(forall ((?t Number)) (HoldsAt (position P3 track2 3) ?t)))
  
  (assert '(not (= track1 track2)))
  
  (assert '(forall ((?pos1 Number) (?pos2 Number) (?t Number) (?train Train) (?track Track))
	    (implies (and 
		      (not (= ?pos1 ?pos2))
		      (HoldsAt (position ?train ?track ?pos1) ?t))
	     (not (HoldsAt (position ?train ?track ?pos2) ?t)))))
  
  
  (assert '(forall ((?train Train) (?track1 Track) (?track2 Track)
		    (?pos Number) (?time Number))
	    (implies (and (not (= ?track1 ?track2))  )
	     (implies (HoldsAt (position ?train ?track1 ?pos) ?time)
	      (or
	       (or (= ?pos 0) (= ?pos 2))
	       (not (exists ((?p Number)) (HoldsAt (position ?train ?track2 ?p) ?time) ))))))))


(defun setup ()
  (setup-snark)
  (declare-dde-commons)
  (assert-common-dde-axioms!))

(defun scenario-1 ()

  (setup)
  

  
  (assert '(not (Clipped 0 (onrails train track1) 1)))
  (assert '(not (Clipped 0 (onrails train track1) 2)))
  (assert '(not (Clipped 1 (onrails train track1) 1)))
  
  (assert '(Clipped 2 (onrails train track1) 3 ))
  (assert '(Clipped 2 (onrails train track1) 4 ))
  (assert '(Clipped 2 (onrails train track1) 5 ))
  (assert '(not (exists ((?t Number))
		 (Happens (switch track2 track1) ?t))))
  (assert '(forall ((?t2 Number))
	    (not  (Clipped 2 (onrails train track2) ?t2))))
  (assert '(Happens (switch track1 track2) 2)))



(defun base ()

  (setup)
  
  (assert '(not (Clipped 0 (onrails train track1) 1)))
  (assert '(not (Clipped 0 (onrails train track1) 2)))
  (assert '(not (Clipped 0 (onrails train track1) 3)))
  (assert '(not (Clipped 0 (onrails train track1) 4)))
  (assert '(not (Clipped 0 (onrails train track1) 5)))
  (assert '(not (Clipped 0 (onrails train track1) 6)))

  )

(defun run-scenario (setup name)
  (funcall setup)
  (print name)
  (if (equalp :PROOF-FOUND (prove `(forall ((?t Number)) (implies (Prior ?t ,*horizon*) (not (HoldsAt (dead P1) ?t))))))
      (print "P1 Dead")
      (print "P1 Alive")) 
  
  (funcall setup)
  (if (equalp :PROOF-FOUND (prove `(forall ((?t Number)) (implies (Prior ?t ,*horizon*) (not (HoldsAt (dead P2) ?t))))))
      (print "P2 Dead")
      (print "P2 Alive"))
 
  (funcall setup)
  (if (equalp :PROOF-FOUND (prove `(exists ((?t Number)) (implies (Prior ?t ,*horizon*) (HoldsAt (dead P3) ?t)))))
      (print "P3 Alive")
      (print "P3 Dead"))
  nil)


(defun run-scenario-1 ()
  (run-scenario #'scenario-1 "Scenario: 1"))

(defun run-base ()

  (print "Base")
 
  ( base)
  (if (equalp :PROOF-FOUND (prove `(exists ((?t Number)) (implies (Prior ?t ,*horizon*) (HoldsAt (dead P1) ?t)))))
      (print "P1 Dead")
      (print "P1 Alive"))
  
   ( base)
   (if (equalp :PROOF-FOUND (prove `(exists ((?t Number)) (implies (Prior ?t ,*horizon*) (HoldsAt (dead P2) ?t)))))
      (print "P2 Dead")
      (print "P2 Alive"))
   
   
    ( base)
    (if (equalp :PROOF-FOUND (prove `(forall ((?t Number)) (implies (Prior ?t ,*horizon*) (not (HoldsAt (dead P3) ?t))))))
      (print "P3 Alive")
      (print "P3 Dead"))
  
  nil)
