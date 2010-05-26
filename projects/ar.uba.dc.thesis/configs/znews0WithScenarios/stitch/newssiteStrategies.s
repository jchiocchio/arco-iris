/*
 * Adaptation script for the News Site example.
 */

module newssite.strategies;

import lib "newssiteTactics.s";

define boolean styleApplies = Model.hasType(M, "ClientT") && Model.hasType(M, "ServerT");
define boolean cViolation = exists c : T.ClientT in M.components | c.experRespTime > M.MAX_RESPTIME;

define set servers = {select s : T.ServerT in M.components | true};
define set unhappyClients = {select c : T.ClientT in M.components | c.experRespTime > M.MAX_RESPTIME};
define int numClients = Set.size({select c : T.ClientT in M.components | true});
define int numUnhappy = Set.size(unhappyClients);
define float numUnhappyFloat = 1.0*numUnhappy;

define boolean hiLoad = exists s : T.ServerT in M.components | s.load > M.MAX_UTIL;
define boolean hiRespTime = exists c : T.ClientT in M.components | c.experRespTime > M.MAX_RESPTIME;
define boolean lowRespTime = exists c : T.ClientT in M.components | c.experRespTime < M.MIN_RESPTIME;

define float totalCost = Model.sumOverProperty("cost", servers);
define boolean hiCost = totalCost >= M.THRESHOLD_COST;

/*TO-DO: agregar logica en el ResponseMeasure para que soporte esto*/
define float avgFidelity = Model.sumOverProperty("fidelity", servers) / Set.size(servers);
define boolean lowFi = avgFidelity < M.THRESHOLD_FIDELITY;

define boolean CONCERN_STILL_BROKEN = AdaptationManagerWithScenarios.isConcernStillBroken("RESPONSE_TIME");

/* For baseline, this Strategy drops the fidelity one notch, and that's it.
 * In the presence of Brute, this strategy probably won't ever get picked.
 */
strategy QuickDirtyReduceResponseTime
[ styleApplies ] {
  t0: (true) -> lowerFidelity(2, 100) @[2000 /*ms*/]{
  	t1: (default) -> TNULL;
  }
}

/* This Strategy will drop fidelity once, observe, then drop again if necessary.
 *
 * Note:  Tested successfully in simulation, znews-brute
 */
strategy BruteReduceResponseTime
[ styleApplies ] {
  t0: (true) -> lowerFidelity(2, 100) @[5000 /*ms*/] {
    t1: (!CONCERN_STILL_BROKEN) -> done;
    t2: (CONCERN_STILL_BROKEN) -> lowerFidelity(2, 100) @[8000 /*ms*/] {
      t2a: (!CONCERN_STILL_BROKEN) -> done;
      t2b: (default) -> TNULL;  // in this case, we have no more steps to take
    }
  }
}

/* This Strategy is simple in that, while it encounters any anomaly in
 * experienced response time, it firsts enlists one new server, then lowers
 * fidelity one step, and quits
 *
 * Note:  Tested successfully in simulation, znews-varied
 */
strategy VariedReduceResponseTime
[ styleApplies && cViolation ] {
  t0: (/*hiLoad*/ cViolation) -> enlistServers(1) @[1000 /*ms*/] {
    t1: (!cViolation) -> done;
    t2: (/*hiRespTime*/ cViolation) -> lowerFidelity(2, 100) @[3000 /*ms*/] {
      t2a: (!cViolation) -> done;
      t2b: (default) -> TNULL;  // in this case, we have no more steps to take
    }
  }
}

/* This Strategy is smarter in that it looks for a percentage of clients with
 * anomalous experienced response time, in which case it enlists a few servers
 * in sequence, then lowers fidelity a few steps, then repeats both once.
 *
 * Note:  Tested successfully in simulation, znews-smarter/2/3
 */
strategy SmarterReduceResponseTime
[ styleApplies && cViolation ] {
  define boolean unhappy = numUnhappyFloat/numClients > M.TOLERABLE_PERCENT_UNHAPPY;

  t0: (unhappy) -> enlistServers(1) @[500 /*ms*/] {
    t1: (!cViolation) -> done;
    t2: (unhappy) -> enlistServers(1) @[2000 /*ms*/] {
      t2a: (!cViolation) -> done;
      t2b: (unhappy) -> lowerFidelity(2, 100) @[2000 /*ms*/] {
        t2b1: (!cViolation) -> done;
        t2b2: (unhappy) -> do[1] t2;
        t2b3: (default) -> TNULL;  // in this case, we have no more steps to take
      }
    }
  }
}

/* This Strategy (experimental!) has the sophistication of reducing fidelity
 * for a percentage of requests depending on percentage of unhappy clients
 *
 * Note:  Tested ?? in simulation, znews-?
 */
strategy SophisticatedReduceResponseTime
[ styleApplies && cViolation && M.SUPPORT_FRACTION_GRADIENT ] {
  define boolean unhappy1 = numUnhappyFloat/numClients > M.UNHAPPY_GRADIENT_1;  // e.g., 10%
  define boolean unhappy2 = numUnhappyFloat/numClients > M.UNHAPPY_GRADIENT_2;  // e.g., 25%
  define boolean unhappy3 = numUnhappyFloat/numClients > M.UNHAPPY_GRADIENT_3;  // e.g., 50%

  t1: (hiLoad) -> enlistServers(2) @[500 /*ms*/] {
    t1a: (!cViolation) -> done;
    t1b: (default) -> TNULL;
  }
  t2: (unhappy1) -> lowerFidelity(1, M.FRACTION_GRADIENT_1) @[500 /*ms*/] {
    t2a: (!cViolation) -> done;
    t2b: (default) -> do[1] t1;
  }
  t3: (unhappy2) -> lowerFidelity(2, M.FRACTION_GRADIENT_2) @[500 /*ms*/] {
    t3a: (!cViolation) -> done;
    t3b: (default) -> do[1] t1;
  }
  t4: (unhappy3) -> lowerFidelity(4, M.FRACTION_GRADIENT_3) @[500 /*ms*/] {
    t4a: (!cViolation) -> done;
    t4b: (unhappy3) -> enlistServers(2) @[1000 /*ms*/] {
      t4b1: (!cViolation) -> done;
      t4b2: (default) -> do[1] t1;
    }
    t4c: (default) -> do[1] t1;
  }
}

/* This Strategy is triggered by the total server costs rising above acceptable
 * threshold; this Strategy reduces the number of active servers
 *
 * Note:  Tested successfully in simulation, znews-reducecost + znews-improvefidelity
 */
strategy ReduceOverallCost
[ styleApplies ] {
  t0: (true) -> dischargeServers(1) @[2000 /*ms*/] {
    t1: (!CONCERN_STILL_BROKEN) -> done;
    t2: (lowRespTime && CONCERN_STILL_BROKEN) -> do[2] t0;
    t3: (default) -> TNULL;
  }
}

/* This Strategy is triggered by overall fidelity being below acceptable
 * threshold; this Strategy raises the fidelity of the servers
 *
 * Note:  Tested successfully in simulation, znews-improvefidelity
 */
strategy ImproveOverallFidelity
[ styleApplies && lowFi ] {
  t0: (true) -> raiseFidelity(2, 100) @[800 /*ms*/] {
    t1: (!CONCERN_STILL_BROKEN) -> done;
    t2: (lowRespTime && CONCERN_STILL_BROKEN) -> do[1] t0;
    t3: (default) -> TNULL;
  }
}
