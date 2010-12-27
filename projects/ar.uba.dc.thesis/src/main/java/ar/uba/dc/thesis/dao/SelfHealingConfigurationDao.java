package ar.uba.dc.thesis.dao;

import java.util.List;

import ar.uba.dc.thesis.atam.scenario.model.Artifact;
import ar.uba.dc.thesis.atam.scenario.model.Environment;
import ar.uba.dc.thesis.repository.SelfHealingConfigurationChangeListener;
import ar.uba.dc.thesis.selfhealing.SelfHealingScenario;

public interface SelfHealingConfigurationDao {

	public List<SelfHealingScenario> getAllScenarios();

	public List<Environment> getAllEnvironments();

	public Environment getEnvironment(String name);

	public List<Artifact> getAllArtifacts();

	public void register(SelfHealingConfigurationChangeListener listener);
}
