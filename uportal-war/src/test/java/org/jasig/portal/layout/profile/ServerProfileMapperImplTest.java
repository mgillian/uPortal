package org.jasig.portal.layout.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.jasig.portal.security.IPerson;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ServerProfileMapperImplTest{

    @Mock HttpServletRequest request;

    @Mock IPerson person;

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    private String serverRegex;

    private Pattern pattern;

    private String nonMatchingHost1;
    private String nonMatchingHost2;
    private String matchingHost1;
    private String matchingHost2;
    private String matchingHost3;


    ServerProfileMapperImpl serverProfileMapper = new ServerProfileMapperImpl();

    @Before
    public void setUp(){

        MockitoAnnotations.initMocks(this);

        //regex matches everything that isn't *.wisconsin.edu*
        serverRegex = "^((?!.\\.wisconsin\\.edu).)*$";
        pattern = Pattern.compile(serverRegex);
        serverProfileMapper.setServerRegex(serverRegex);
        nonMatchingHost1 = "my.wisconsin.edu";
        nonMatchingHost2 = "predev.wisconsin.edu/profile";
        matchingHost1 = "my.wisc.edu";
        matchingHost2 = "my.university-of-wisconsin.edu/money";
        matchingHost3 = "my.uwrf.edu";

        //Make sure that matching hosts match
        assertTrue(pattern.matcher(matchingHost1).matches());
        assertTrue(pattern.matcher(matchingHost2).matches());
        assertTrue(pattern.matcher(matchingHost3).matches());

        //Make sure that unmatching hosts don't match
        assertFalse(pattern.matcher(nonMatchingHost1).matches());
        assertFalse(pattern.matcher(nonMatchingHost2).matches());
    }

    @Test
    public void testNullorEmptyRegex(){
        thrown.expect(NullPointerException.class);
        serverProfileMapper.setServerRegex(null);
        thrown.expect(IllegalArgumentException.class);
        serverProfileMapper.setServerRegex("");
    }

    @Test
    public void testNullorEmptyProfile(){
        thrown.expect(NullPointerException.class);
        serverProfileMapper.setProfile(null);
        thrown.expect(IllegalArgumentException.class);
        serverProfileMapper.setProfile("");
    }

    @Test
    public void testNullParametersToGetProfileName(){
        thrown.expect(NullPointerException.class);
        serverProfileMapper.getProfileFname(null, request);
        serverProfileMapper.getProfileFname(person, null);
        when(person.getUserName()).thenReturn(null);
        serverProfileMapper.getProfileFname(person, request);
    }

    @Test
    public void testDefaultProfileExistsForServerMatch(){
        when(request.getServerName()).thenReturn(matchingHost1);
        assertNotNull(serverProfileMapper.getProfileFname(person, request));
    }

    @Test
    public void testGetProfileFnameWithSpecifiedProfile(){
        String buckyProfile = "bucky";
        serverProfileMapper.setProfile(buckyProfile);
        when(request.getServerName()).thenReturn(matchingHost1);
        assertEquals(serverProfileMapper.getProfileFname(person, request),
          buckyProfile);
    }

    @Test
    public void testGetProfileNoServerMatch(){
      when(request.getServerName()).thenReturn(nonMatchingHost1);
      assertNull(serverProfileMapper.getProfileFname(person, request));
    }

}
