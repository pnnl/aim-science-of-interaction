package gov.pnnl.aim.nmr;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import gov.pnnl.aim.discovery.streams.ASIConnector;

@Controller
public class NMRRestService {
	static {
		LogManager.getLogManager().reset();
		System.setProperty("java.util.logging.config.file", "");
	}

	private FeedbackSender feedbackSender = null;

	private NewNMRSampleStreamMonitor monitor = null;

	private NewNMRSampleSimulator simulator;

	private final boolean isSimulation = false;

	private final static int MAX_HISTORY = 20;

	/**
	 *
	 * @return
	 */
	private boolean isSimulation() {
		return isSimulation;
	}

	private void initASI() {
		Logger.getGlobal().setLevel(Level.WARNING);

		try {
			if (this.monitor == null) {
				System.out.println("Init ASI connection");
				ASIConnector.initConnector(NMRConfig.ASI_USER, NMRConfig.ASI_PWD);
				this.monitor = new NewNMRSampleStreamMonitor();
				this.feedbackSender = new FeedbackSender();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start monitoring of the ASI for new messages about NMR samples
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/nmr/init", method = RequestMethod.GET)
	public @ResponseBody String initStreams(final HttpServletRequest request, final HttpServletResponse response) {
		initASI();
		return "Success!";
	}

	/**
	 * @return the list of compound names
	 */
	@RequestMapping(value = "/nmr/compound/labels", method = RequestMethod.GET)
	public @ResponseBody List<String> getCompoundLabels() {
		if (isSimulation()) {
			simulator = new NewNMRSampleSimulator();
			return simulator.getCompundNames();
		}

		initASI();
		return monitor.getCompundNames();
	}

	/**
	 * @return the list of compound names
	 * @throws Exception
	 * @throws JsonIOException
	 * @throws JsonSyntaxException
	 */
	@RequestMapping(value = "/nmr/reactor/state", method = RequestMethod.GET)
	public @ResponseBody NMRReactorState getReactorState() throws Exception {
		if (isSimulation()) {
			URL url = this.getClass().getResource("test.json");
			Gson gson = new Gson();
			OldNMRReactorState state = gson.fromJson(new InputStreamReader(url.openStream()), OldNMRReactorState.class);
			NMRReactorState s = new NMRReactorState();
			s.setHistory(trimHistory(state.getHistory()));
			s.updateRanges();
			return s;
		}

		initASI();
		NMRReactorState state = monitor.getNMRReactorState();
		state.setHistory(trimHistory(state.getHistory()));
		state.updateRanges();
		return state;
	}

	/**
	 * @return the list of compound names
	 * @throws Exception
	 * @throws JsonIOException
	 * @throws JsonSyntaxException
	 */
	@RequestMapping(value = "/nmr/reactor/compoundHistory", method = RequestMethod.POST)
	public @ResponseBody NMRCompoundHistory getCompoundHistory(@RequestBody final String compound) throws Exception {
		if (isSimulation()) {
			URL url = this.getClass().getResource("test.json");
			Gson gson = new Gson();
			OldNMRReactorState state = gson.fromJson(new InputStreamReader(url.openStream()), OldNMRReactorState.class);
			NMRReactorState s = new NMRReactorState();
			s.setHistory(state.getHistory());
			return createCompoundHistory(s, compound);
		}

		initASI();
		NMRReactorState state = monitor.getNMRReactorState();
		return createCompoundHistory(state, compound);
	}

	/**
	 * @param s
	 * @return
	 */
	private NMRCompoundHistory createCompoundHistory(final NMRReactorState state, final String compound) {
		Map<String, Double> map = new HashMap<>();
		List<NMRSample> history = state.getHistory();
		for (NMRSample s : history) {
			List<NMRCompoundMeasure> measures = s.getMeasures();
			for (NMRCompoundMeasure m : measures) {
				if (m.getCompoundName().equals(compound)) {
					double concentration = m.getConcentration();
					map.put(s.getSampleID(), concentration);
					break;
				}
			}
		}

		NMRCompoundHistory h = new NMRCompoundHistory();
		h.setMap(map);
		return h;
	}

	private List<NMRSample> trimHistory(final List<NMRSample> full) {
		if (full.size() <= MAX_HISTORY) {
			return full;
		}

		return full.subList(0, MAX_HISTORY);
	}

	/**
	 * Provides a list of samples to the client
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/nmr/samples", method = RequestMethod.GET)
	public @ResponseBody List<SamplePrediction> getSamples(final HttpServletRequest request,
			final HttpServletResponse response) {
		List<SamplePrediction> samples = new ArrayList<SamplePrediction>();
		samples.add(new SamplePrediction());
		return samples;
	}

	/**
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/nmr/feedback/compound", method = RequestMethod.POST)
	public @ResponseBody String recieveCompoundFeedback(@RequestBody final FeedbackCompounds feedback) {

		System.out.println(feedback.getSampleID());
		System.out.println(feedback.getCompoundName());
		System.out.println(feedback.isCompoundPresence());

		try {
			NMRReactorState state = getReactorState();
			NMRSample sample = state.getSample(feedback.getSampleID());
			feedbackSender.sendCompoundFeedback(feedback.getSampleID(), feedback.getCompoundName(),
					feedback.isCompoundPresence(), sample);
		} catch (Exception e) {
			e.printStackTrace();
			return "Problem";
		}

		return "Thanks";
	}

	/**
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/nmr/feedback/errorstatus", method = RequestMethod.POST)
	public @ResponseBody String recieveErrorStatusFeedback(@RequestBody final FeedbackErrorCondition feedback) {

		System.out.println(feedback.getSampleID());
		System.out.println(feedback.getErrorCondition());
		System.out.println(feedback.isErrorCorrect());

		try {
			feedbackSender.sendErrorStatusFeedback(feedback.getSampleID(), feedback.getErrorCondition(),
					feedback.isErrorCorrect());
		} catch (Exception e) {
			e.printStackTrace();
			return "Problem";
		}

		return "Thanks";
	}
}
