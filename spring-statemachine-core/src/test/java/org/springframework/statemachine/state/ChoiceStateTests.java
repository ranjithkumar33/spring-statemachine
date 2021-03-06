/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.statemachine.state;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.EnumSet;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.AbstractStateMachineTests;
import org.springframework.statemachine.EnumStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachineSystemConstants;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.util.ObjectUtils;

public class ChoiceStateTests extends AbstractStateMachineTests {

	@Override
	protected AnnotationConfigApplicationContext buildContext() {
		return new AnnotationConfigApplicationContext();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFirst() {
		context.register(BaseConfig.class, Config1.class);
		context.refresh();
		EnumStateMachine<TestStates,TestEvents> machine =
				context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, EnumStateMachine.class);
		assertThat(machine, notNullValue());
		machine.start();
		machine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).setHeader("choice", "s30").build());

		assertThat(machine.getState().getIds(), contains(TestStates.S30));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThen1() {
		context.register(BaseConfig.class, Config1.class);
		context.refresh();
		EnumStateMachine<TestStates,TestEvents> machine =
				context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, EnumStateMachine.class);
		assertThat(machine, notNullValue());
		machine.start();
		machine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).setHeader("choice", "s31").build());

		assertThat(machine.getState().getIds(), contains(TestStates.S31));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThen2() {
		context.register(BaseConfig.class, Config1.class);
		context.refresh();
		EnumStateMachine<TestStates,TestEvents> machine =
				context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, EnumStateMachine.class);
		assertThat(machine, notNullValue());
		machine.start();
		machine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).setHeader("choice", "s32").build());

		assertThat(machine.getState().getIds(), contains(TestStates.S32));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testLast() {
		context.register(BaseConfig.class, Config1.class);
		context.refresh();
		EnumStateMachine<TestStates,TestEvents> machine =
				context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, EnumStateMachine.class);
		assertThat(machine, notNullValue());
		machine.start();
		machine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());

		assertThat(machine.getState().getIds(), contains(TestStates.S33));
	}

	@Configuration
	@EnableStateMachine
	static class Config1 extends EnumStateMachineConfigurerAdapter<TestStates, TestEvents> {

		@Override
		public void configure(StateMachineStateConfigurer<TestStates, TestEvents> states) throws Exception {
			states
				.withStates()
					.initial(TestStates.SI)
					.states(EnumSet.allOf(TestStates.class))
					.choice(TestStates.S3)
					.end(TestStates.SF);
		}

		@Override
		public void configure(StateMachineTransitionConfigurer<TestStates, TestEvents> transitions) throws Exception {
			transitions
				.withExternal()
					.source(TestStates.SI)
					.target(TestStates.S3)
					.event(TestEvents.E1)
					.and()
				.withChoice()
					.source(TestStates.S3)
					.first(TestStates.S30, s30Guard())
					.then(TestStates.S31, s31Guard())
					.then(TestStates.S32, s32Guard())
					.last(TestStates.S33);
		}

		@Bean
		public Guard<TestStates, TestEvents> s30Guard() {
			return new ChoiceGuard("s30");
		}

		@Bean
		public Guard<TestStates, TestEvents> s31Guard() {
			return new ChoiceGuard("s31");
		}

		@Bean
		public Guard<TestStates, TestEvents> s32Guard() {
			return new ChoiceGuard("s32");
		}

	}

	private static class ChoiceGuard implements Guard<TestStates, TestEvents> {

		private final String match;

		public ChoiceGuard(String match) {
			this.match = match;
		}

		@Override
		public boolean evaluate(StateContext<TestStates, TestEvents> context) {
			return ObjectUtils.nullSafeEquals(match, context.getMessageHeaders().get("choice", String.class));
		}
	}

}
