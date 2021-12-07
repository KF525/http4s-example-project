import Read from "./Read";
import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Link
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
            <Link to="/">
              <img src="https://play-lh.googleusercontent.com/0_ixZOlXHE0DLR207sHfk-tX-XbkyiBqafbVqenrhlYCBmbDdzSSrsecjtuzJPcDgVl-nYO9kZYLM-o=s400" alt='Compound Poem' />
            </Link>
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