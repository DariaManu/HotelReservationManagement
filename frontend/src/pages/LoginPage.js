import {useContext, useRef, useState} from "react";
import {AuthContext} from "../commons/AuthContextProvider";
import {Card, CardBody, Col, Form, Row} from "react-bootstrap";
import {Navigate, NavLink} from "react-router-dom";

export const LoginPage = () => {
    const [email, setEmail] = useState();
    const [password, setPassword] = useState();
    const {auth, login} = useContext(AuthContext);
    const [incorrectCredentials, setIncorrectCredentials] = useState(false);
    const [errorMessage, setErrorMessage] = useState();
    const [emailRequired, setEmailRequired] = useState(false);
    const [passwordRequired, setPasswordRequired] = useState(false);

    const handleClickOnSubmitButton = async (e) => {
        e.preventDefault();
        let loginFailed = false;

        if (!email) {
            setEmailRequired(true);
            return;
        } else {
            setEmailRequired(false);
        }
        if (!password) {
            setPasswordRequired(true);
            return;
        } else {
            setPasswordRequired(false);
        }

        await login({email: email, password: password})
            .catch((error) => {
                loginFailed = true;
                if (error.response && error.response.status === 400) {
                    setIncorrectCredentials(true);
                    setErrorMessage(error.response.data);
                }
            })
            .then(() => {
                if (!loginFailed) {
                    setIncorrectCredentials(false);
                    return <Navigate to={"/home"}/>
                }
            });
    };

    if (auth) {
        return <Navigate to={"/home"}/>
    }

    return (
        <div className="container d-flex flex-column justify-content-center text-center authForm w-25 mt-5">
            <h2 className="mb-4"><b> Login </b></h2>
            <div className="d-flex">
                <Card className="container">
                    <CardBody className="formBody">
                        <Form>
                            <Row className="formRow">
                                <Col className="col-md-10 mx-auto">
                                    <Form.Label>Email</Form.Label>
                                    <Form.Control
                                        type="text"
                                        id="emailInput"
                                        onChange={(e) => {setEmail(e.target.value)}}
                                        required
                                    />
                                    {emailRequired && (<span className="error">Email is required</span>)}
                                </Col>
                            </Row>
                            <Row className="formRow">
                                <Col className="col-md-10 mx-auto">
                                    <Form.Label>Password</Form.Label>
                                    <Form.Control
                                        type="password"
                                        id="passwordInput"
                                        onChange={(e) => {setPassword(e.target.value)}}
                                        required
                                    />
                                    {passwordRequired && (<span className="error">Password is required</span>)}
                                    {incorrectCredentials && (<p className="error">{errorMessage}</p>)}
                                </Col>
                            </Row>
                            <Row className="formRow">
                                <Col className="col-md-4 mx-auto">
                                    <Form.Control
                                        id="submitCredentialsButton"
                                        type="submit"
                                        value="Log In"
                                        className="btn btn-primary"
                                        onClick={handleClickOnSubmitButton}
                                    />
                                </Col>
                            </Row>
                        </Form>
                    </CardBody>
                </Card>
            </div>
        </div>
    )
}