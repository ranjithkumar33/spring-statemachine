package demo.washer;

import org.springframework.context.annotation.Configuration;
import org.springframework.shell.Bootstrap;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.StateConfigurer.History;

@Configuration
public class Application  {

	@Configuration
	@EnableStateMachine
	static class StateMachineConfig
			extends EnumStateMachineConfigurerAdapter<States, Events> {

//tag::snippetAA[]
		@Override
		public void configure(StateMachineStateConfigurer<States, Events> states)
				throws Exception {
			states
				.withStates()
					.initial(States.RUNNING)
					.state(States.POWEROFF)
					.end(States.END)
					.and()
					.withStates()
						.parent(States.RUNNING)
						.initial(States.WASHING)
						.state(States.RINSING)
						.state(States.DRYING)
						.history(States.HISTORY, History.SHALLOW);
		}
//end::snippetAA[]

//tag::snippetAB[]
		@Override
		public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
				throws Exception {
			transitions
				.withExternal()
					.source(States.WASHING)
					.target(States.RINSING)
					.event(Events.RINSE)
					.and()
				.withExternal()
					.source(States.RINSING)
					.target(States.DRYING)
					.event(Events.DRY)
					.and()
				.withExternal()
					.source(States.RUNNING)
					.target(States.POWEROFF)
					.event(Events.CUTPOWER)
					.and()
				.withExternal()
					.source(States.POWEROFF)
					.target(States.HISTORY)
					.event(Events.RESTOREPOWER);
		}
//end::snippetAB[]

	}

//tag::snippetB[]
	public static enum States {
	    RUNNING, HISTORY, END,
	    WASHING, RINSING, DRYING,
	    POWEROFF
	}
//end::snippetB[]

//tag::snippetC[]
	public static enum Events {
	    RINSE, DRY,
	    RESTOREPOWER, CUTPOWER
	}
//end::snippetC[]

	public static void main(String[] args) throws Exception {
		Bootstrap.main(args);
	}

}
