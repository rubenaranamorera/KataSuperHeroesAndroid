/*
 * Copyright (C) 2015 Karumi.
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

package com.karumi.katasuperheroes;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.ui.view.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.karumi.katasuperheroes.matchers.RecyclerViewItemsCountMatcher.recyclerViewHasItemCount;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public DaggerMockRule<MainComponent> daggerRule =
            new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
                    new DaggerMockRule.ComponentSetter<MainComponent>() {
                        @Override
                        public void setComponent(MainComponent component) {
                            SuperHeroesApplication app =
                                    (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation()
                                            .getTargetContext()
                                            .getApplicationContext();
                            app.setComponent(component);
                        }
                    });

    @Rule
    public IntentsTestRule<MainActivity> activityRule =
            new IntentsTestRule<>(MainActivity.class, true, false);

    @Mock
    SuperHeroesRepository repository;

    @Test
    public void showsEmptyCaseIfThereAreNoSuperHeroes() {
        givenThereAreNoSuperHeroes();

        startActivity();

        onView(withText("¯\\_(ツ)_/¯")).check(matches(isDisplayed()));
    }

    @Test
    public void shows5rowsIfThereAre5SuperHeroes() {
        givenSuperHeroes(5);

        startActivity();

        onView(withId(R.id.recycler_view)).check(matches(recyclerViewHasItemCount(5)));
    }

    @Test
    public void showsRocIfSuperHeroNameIsRoc() {
        givenOnlyRocSuperHero();

        startActivity();

        onView(withText("Roc")).check(matches(isDisplayed()));
    }

    @Test
    public void opensDetailWhenClickingSuperHero() {
        givenOnlyRocSuperHero();

        startActivity();

        givenRocDetail();

        onView(withText("Roc")).perform(click());

        onView(withId(R.id.tv_super_hero_description)).check(matches(isDisplayed()));
    }

    @Test
    public void opensDetailWhenClickingSuperHeroInMultipleList() {
        givenSuperHeroes(10);

        startActivity();

        givenRocDetail();

        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));

        onView(withId(R.id.tv_super_hero_description)).check(matches(isDisplayed()));
    }

    private void givenRocDetail() {
        when(repository.getByName("Roc")).thenReturn(giveMockRocSuperHero());

    }

    private void givenOnlyRocSuperHero() {
        List<SuperHero> heroList = new ArrayList<SuperHero>();

        heroList.add(giveMockRocSuperHero());

        when(repository.getAll()).thenReturn(heroList);
    }

    private List<SuperHero> givenSuperHeroes(int heroNumber) {

        List<SuperHero> heroList = new ArrayList<SuperHero>();

        for (int i = 0; i < heroNumber; i++) {
            heroList.add(giveMockRocSuperHero());
        }

        when(repository.getAll()).thenReturn(heroList);

        return heroList;
    }

    private SuperHero giveMockRocSuperHero() {
        return new SuperHero("Roc", "https://i.annihil.us/u/prod/marvel/i/mg/9/b0/537bc2375dfb9.jpg",
                true, "Roc es un superheroi mol gracios! Papa Pipo Pipo Papa!");
    }

    private void givenThereAreNoSuperHeroes() {
        when(repository.getAll()).thenReturn(Collections.<SuperHero>emptyList());
    }

    private MainActivity startActivity() {
        return activityRule.launchActivity(null);
    }
}