import Read from "./Read";
import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  NavLink
} from "react-router-dom";
import {Create} from "./Create";
import './App.css';

const About = () => <div>About</div>

function App() {
  return (
    <Router>
      <div className="Navigation">
        <ul>
          <li>
            <NavLink to="/">
              <img src="https://play-lh.googleusercontent.com/0_ixZOlXHE0DLR207sHfk-tX-XbkyiBqafbVqenrhlYCBmbDdzSSrsecjtuzJPcDgVl-nYO9kZYLM-o=s400" alt='Compound Poem' />
            </NavLink>
          </li>
          <li>
            <NavLink to="/about">About</NavLink>
          </li>
          <li>
            <NavLink to="/read">Read</NavLink>
          </li>
          <li>
            <NavLink to="/create">Create</NavLink>
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