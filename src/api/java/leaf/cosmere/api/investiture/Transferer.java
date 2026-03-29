package leaf.cosmere.api.investiture;

import leaf.cosmere.api.manifestation.Manifestation;

public class Transferer
{
	protected KineticInvestiture investIn;
	protected KineticInvestiture investOut;
	private int rate;
	private int cycles = Integer.MAX_VALUE;
	private int cycNum = 0;

	private double killAmount;

	public Transferer(KineticInvestiture investIn, IInfuseContainer containerOut, int transfeRate, double decayRate, int cycles, double killPoint)
	{
		this(investIn, containerOut, transfeRate, decayRate);
		this.cycles = cycles;
		this.killAmount = Math.min(killPoint, containerOut.getMaxBEU());
	}
	public Transferer(KineticInvestiture investIn, IInfuseContainer containerOut, int transferRate, double decayRate)
	{
		this.investIn = investIn;
		InvHelpers.Shard shard = investIn.getShard();
		Manifestation[] man = investIn.getApplicableManifestations();
		this.investOut = new KineticInvestiture(containerOut, shard, source, investIn.removeBEU(transferRate), man, decayRate);
		rate = transferRate;
		InvHelpers.TransferHelper.addTransferer(this);
		this.killAmount = containerOut.getMaxBEU();
	}

	public Transferer(KineticInvestiture investIn, IInvContainer containerOut, int transferRate)
	{
		this(investIn, containerOut, transferRate, 0);
	}

	protected Transferer(KineticInvestiture investIn, KineticInvestiture investOut, int transferRate, int cycles, double killPoint)
	{
		this.investOut = investOut;
		this.investIn = investIn;
		this.rate = transferRate;
		this.cycles = cycles;
		this.killAmount = killPoint;
	}

	protected Transferer(KineticInvestiture investIn, KineticInvestiture investOut, int transferRate)
	{
		this(investIn, investOut, transferRate, Integer.MAX_VALUE, investOut.getContainer().getMaxBEU());
	}

	public void transfer()
	{
		int transfer1;
		if((cycNum + 1) * rate > killAmount)
		{
			transfer1 = ((cycNum + 1) * rate) - killAmount;
		}
		else
		{
			transfer1 = rate;
		}
		cycNum++;
		investOut.addBEU(investIn.removeBEU(transfer1));
		investIn.reattach();
		if (cycNum == cycles || cycNum * rate >= killAmount)
		{
			endTransfer();
		}
	}

	public int getRate()
	{
		return rate;
	}

	public KineticInvestiture getInvestIn()
	{
		return investIn;
	}

	public KineticInvestiture getInvestOut()
	{
		return investOut;
	}

	public void setKillAmount(double amount)
	{
		killAmount = amount;
	}

	public void endTransfer()
	{
		investIn = null;
		investOut = null;
		InvHelpers.TransferHelper.removeTransferer(this);
	}
	public void clean()
	{
		if(investIn.getBEU() == 0)
		{
			investIn = null;
		}
	}
}
