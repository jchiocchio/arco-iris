package ar.uba.dc.thesis.rainbow.constraint;

import org.sa.rainbow.scenario.model.RainbowModelWithScenarios;

import ar.uba.dc.thesis.atam.Environment.HeuristicType;
import ar.uba.dc.thesis.common.Validatable;

public interface Constraint extends Validatable {

	boolean holds(RainbowModelWithScenarios rainbowModelWithScenarios, HeuristicType most);
}
