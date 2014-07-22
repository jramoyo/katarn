package com.jramoyo.katarn;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MsgHandlerMethodResolverTest {

    private MsgHandlerMethodResolver resolver;

    @Mock
    private MethodContext methodContext;

    @Before
    public void setup() {
        resolver = new MsgHandlerMethodResolver("prefix_");
        Mockito.when(methodContext.toString()).thenReturn("method");
    }

    @Test(expected = IllegalArgumentException.class)
    public void init_nullPackages_throwsException() throws Exception {
        resolver.init((String[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void init_emptyPackages_throwsException() throws Exception {
        resolver.init(new String[]{});
    }

    @Test(expected = MsgHandlingException.class)
    public void init_duplicateMapping_throwsException() throws Exception {
        resolver.init("com.jramoyo.katarn.scenarios.resolver.error.duplicate");
    }

    @Test(expected = MsgHandlingException.class)
    public void init_noMapping_throwsException() throws Exception {
        resolver.init("com.jramoyo.katarn.scenarios.resolver.error.nomapping");
    }

    @SuppressWarnings("boxing")
    @Test
    public void init_populatesMappingTable() throws Exception {
        resolver.init("com.jramoyo.katarn.scenarios.resolver.success");
        assertThat(resolver.mapping.isEmpty(), equalTo(false));
        assertThat(resolver.mapping.get("mapping1").toString(), equalTo("MethodContext [owner=Success1, method=get]"));
        assertThat(resolver.mapping.get("mapping2").toString(), equalTo("MethodContext [owner=Success2, method=delete]"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolve_nullDestination_throwsException() throws Exception {
        resolver.resolve(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolve_emptyDestination_throwsException() throws Exception {
        resolver.resolve("");
    }

    @Test(expected = MsgHandlingException.class)
    public void resolve_wrongType_throwsException() throws Exception {
        resolver.resolve("prefix_xxx_mapping1");
    }

    @Test(expected = MsgHandlingException.class)
    public void resolve_unknownDestination_throwsException() throws Exception {
        resolver.resolve("prefix_get_unknown");
    }

    @Test
    public void resolve_returnsCorrectMethod() throws Exception {
        resolver.mapping.put("mapping1", methodContext);
        assertThat(resolver.resolve("prefix_mapping1").toString(), equalTo("method"));
    }
}
