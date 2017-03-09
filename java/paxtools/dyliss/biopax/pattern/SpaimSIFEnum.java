package dyliss.biopax.pattern;

import java.util.Arrays;
import java.util.List;

import org.biopax.paxtools.pattern.miner.SIFMiner;
import org.biopax.paxtools.pattern.miner.SIFType;

import dyliss.biopax.pattern.miner.CSCOBothControllerAndParticipantMiner;
import dyliss.biopax.pattern.miner.CSCOButIsParticipantMiner;
import dyliss.biopax.pattern.miner.CSCOThroughBindingSmallMoleculeMiner;
import dyliss.biopax.pattern.miner.CSCOThroughControllingSmallMoleculeMiner;
import dyliss.biopax.pattern.miner.CSCOThroughDegradationMiner;
import dyliss.biopax.pattern.miner.CatalysisPrecedesMiner;
import dyliss.biopax.pattern.miner.ChemicalAffectsThroughBindingMiner;
import dyliss.biopax.pattern.miner.ChemicalAffectsThroughControlMiner;
import dyliss.biopax.pattern.miner.ConsumptionControlledByMiner;
import dyliss.biopax.pattern.miner.ControlsExpressionMiner;
import dyliss.biopax.pattern.miner.ControlsExpressionWithConvMiner;
import dyliss.biopax.pattern.miner.ControlsPhosphorylationMiner;
import dyliss.biopax.pattern.miner.ControlsProductionOfMiner;
import dyliss.biopax.pattern.miner.ControlsStateChangeOfMiner;
import dyliss.biopax.pattern.miner.ControlsTransportMiner;
import dyliss.biopax.pattern.miner.ControlsTransportOfChemicalMiner;
import dyliss.biopax.pattern.miner.InComplexWithMiner;
import dyliss.biopax.pattern.miner.InteractsWithMiner;
import dyliss.biopax.pattern.miner.NeighborOfMiner;
import dyliss.biopax.pattern.miner.ReactsWithMiner;
import dyliss.biopax.pattern.miner.TestCSCOThroughControllingSmallMoleculeMiner;
import dyliss.biopax.pattern.miner.TestCSCOThroughControllingSmallMoleculeMiner2;
import dyliss.biopax.pattern.miner.UsedToProduceMiner;
 

/**
 * Enum for representing supported SIF edge types.
 *
 * @author Ozgun Babur
 */
public enum SpaimSIFEnum implements SIFType
{
	
	// fm : KRF intermediary model: need probably multiple SIFTYPE
	// for generating a sub graph (a list of sif interaction) from a complexe pattern
	
	TEST_CONTROLS_STATE_CHANGE_OF("First protein is controlling a reaction that changes the state of " +
			"the second protein.", true, TestCSCOThroughControllingSmallMoleculeMiner.class),
	
	TEST_CONTROLS_STATE_CHANGE_OF2("First protein is controlling a reaction that changes the state of " +
			"the second protein.", true, TestCSCOThroughControllingSmallMoleculeMiner2.class),
	
	/*
	 CONTROLS_STATE_CHANGE_OF("First protein is controlling a reaction that changes the state of " +
				"the second protein.", true, CSCOThroughControllingSmallMoleculeMiner.class), 
	 */
	 CONTROLS_STATE_CHANGE_OF("First protein is controlling a reaction that changes the state of " +
		"the second protein.", true, ControlsStateChangeOfMiner.class,
		CSCOButIsParticipantMiner.class, CSCOBothControllerAndParticipantMiner.class,
		CSCOThroughControllingSmallMoleculeMiner.class, CSCOThroughBindingSmallMoleculeMiner.class,
		CSCOThroughDegradationMiner.class), 
	 
 
	
	CONTROLS_TRANSPORT_OF("First protein is controlling a reaction that changes the cellular " +
		"location of the second protein.", true, ControlsTransportMiner.class),
	CONTROLS_PHOSPHORYLATION_OF("First protein is controlling a reaction that changes the " +
		"phosphorylation status of the second protein.", true, ControlsPhosphorylationMiner.class),
	CONTROLS_EXPRESSION_OF("First protein is controlling a conversion or a template reaction that" +
		"changes expression of the second protein.", true, ControlsExpressionMiner.class,
		ControlsExpressionWithConvMiner.class),
	CATALYSIS_PRECEDES("First protein is controlling a reaction whose output molecule is input" +
		" to another reaction controlled by the second protein.", true,
		CatalysisPrecedesMiner.class),
	IN_COMPLEX_WITH("Proteins appear as members of the same complex.", false,
		InComplexWithMiner.class),
	INTERACTS_WITH("Proteins appear as participants of the same MolecularInteraction.", false,
		InteractsWithMiner.class),
	NEIGHBOR_OF("Proteins appear as participants or controllers of the same interaction.", false,
		NeighborOfMiner.class),
	CONSUMPTION_CONTROLLED_BY("The small molecule is consumed by a reaction that is controlled by" +
		" a protein.", true, ConsumptionControlledByMiner.class),
	CONTROLS_PRODUCTION_OF("The protein is controlling a reaction of which the small molecule is " +
		"an output.", true, ControlsProductionOfMiner.class),
	CONTROLS_TRANSPORT_OF_CHEMICAL("The protein is controlling a reaction that changes cellular " +
		"location of the small molecule.", true, ControlsTransportOfChemicalMiner.class),
	CHEMICAL_AFFECTS("A small molecule has an effect on a protein.", true,
		ChemicalAffectsThroughControlMiner.class, ChemicalAffectsThroughBindingMiner.class),
	REACTS_WITH("A small molecule is input to a biochemical reaction together with another small " +
		"molecule. None of the molecules are also output.", false, ReactsWithMiner.class),
	USED_TO_PRODUCE("A small molecule is input to a biochemical reaction that produces " +
		"another small molecule. Both small molecules appear at only one side of the reaction.",
		true, UsedToProduceMiner.class),
	;
 
	/**
	 * Constructor with parameters.
	 * @param description description of the edge type
	 * @param directed whether the edge type is directed
	 */
	SpaimSIFEnum(String description, boolean directed, Class<? extends SIFMiner>... miners)
	{
		this.description = description;
		this.directed = directed;
		this.miners = Arrays.asList(miners);
	}

	/**
	 * Description of the SIF type.
	 */
	private String description;

	/**
	 * Some SIF edges are directed and others are not.
	 */
	private boolean directed;

	/**
	 * SIF Miners to use during a search.
	 */
	private List<Class<? extends SIFMiner>> miners;

	/**
	 * Tag of a SIF type is derived from the enum name.
	 * @return tag
	 */
	public String getTag()
	{
		return name().toLowerCase().replaceAll("_", "-");
	}

	/**
	 * Asks if the edge is directed.
	 * @return true if directed
	 */
	public boolean isDirected()
	{
		return directed;
	}

	/**
	 * Gets the description of the SIF type.
	 * @return description
	 */
	public String getDescription()
	{
		return description;
	}

	public List<Class<? extends SIFMiner>> getMiners()
	{
		return miners;
	}

	public static SpaimSIFEnum typeOf(String tag)
	{
		tag = tag.toUpperCase().replaceAll("-", "_");
		SpaimSIFEnum type = null;
		try
		{
			type = valueOf(tag);
		}
		catch (IllegalArgumentException e){}
		return type;
	}
}