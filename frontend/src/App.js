import Read from "./Read";
import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Link
} from "react-router-dom";
import {Create} from "./Create";

const About = () => <div>About</div>

function App() {
  return (
    <Router>
      <div>
        <ul>
          <li>
            <Link to="/">Home</Link>
          </li>
          <li>
            <Link to="/about">About</Link>
          </li>
          <li>
            <Link to="/read">Read</Link>
          </li>
          <li>
            <Link to="/create">Create</Link>
          </li>
        </ul>

        <Routes>
          <Route path="/about" element={<About/>}/>
          <Route path="/read" element={<Read/>}/>
          <Route path="/create" element={<Create/>}/>
        </Routes>
      </div>
    </Router>
  );
}

export default App;