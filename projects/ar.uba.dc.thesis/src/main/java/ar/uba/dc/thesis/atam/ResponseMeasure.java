package ar.uba.dc.thesis.atam;

import org.acmestudio.acme.model.IAcmeModel;

import ar.uba.dc.thesis.common.ThesisPojo;
import ar.uba.dc.thesis.rainbow.constraint.SinglePropertyInvolvedConstraint;

public class ResponseMeasure extends ThesisPojo {

	private final String description;

	private final SinglePropertyInvolvedConstraint constraint;

	public ResponseMeasure(String description, SinglePropertyInvolvedConstraint constraint) {
		super();
		this.description = description;
		this.constraint = constraint;

		this.validate();
	}

	public String getDescription() {
		return this.description;
	}

	public SinglePropertyInvolvedConstraint getConstraint() {
		return this.constraint;
	}

	public void validate() {
		// Do nothing
	}

	public boolean holds(IAcmeModel acmeModel) {
		return this.constraint.holds(acmeModel);
	}
}