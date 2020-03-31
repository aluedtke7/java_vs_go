package main

import (
	"context"
	"encoding/json"
	"log"
	"net/http"
	"os"
	"os/signal"
	"time"

	"github.com/dgrijalva/jwt-go"
	"github.com/gorilla/mux"
	"github.com/urfave/negroni"
)

const defaultPort = "8080"

var (
	router *mux.Router
	neg    *negroni.Negroni
	ctx    context.Context
)

func main() {
	router = mux.NewRouter()
	neg = negroni.New()
	neg.UseHandler(router)
	server()
}

func generateToken(username string) (string, error) {
	// Create token
	token := jwt.New(jwt.SigningMethodHS256)

	// Set claims
	// This is the information which frontend can use
	// The backend can also decode the token and get admin etc.
	claims := token.Claims.(jwt.MapClaims)
	claims["sub"] = 1
	claims["name"] = username
	claims["admin"] = true
	claims["exp"] = time.Now().Add(time.Minute * 15).Unix()

	// Generate encoded token and send it as response.
	// The signing string should be secret (a generated UUID works too)
	t, err := token.SignedString([]byte("secret"))
	if err != nil {
		return "", err
	}

	return t, nil
}

type user struct {
	Name     string `json:"username"`
	Password string `json:"password"`
}

func loginHandler(w http.ResponseWriter, r *http.Request) {
	var u user
	decoder := json.NewDecoder(r.Body)
	err := decoder.Decode(&u)
	token, err := generateToken(u.Name)
	// w.Header().Set("Content-Type", "application/json")
	if err == nil {
		w.WriteHeader(http.StatusCreated)
		// response, _ := json.Marshal(tokens)
		// _, _ = w.Write(response)
		_, _ = w.Write([]byte(token))
	} else {
		w.WriteHeader(http.StatusBadRequest)
		// response, _ := json.Marshal("Token error")
		// _, _ = w.Write(response)
		_, _ = w.Write([]byte("Token error"))
	}
}

func server() {
	router.HandleFunc("/login", loginHandler).Methods("POST")
	http.Handle("/", neg)
	srv := &http.Server{
		Addr: "0.0.0.0:" + defaultPort,
		// Good practice to set timeouts to avoid Slowloris attacks.
		WriteTimeout: time.Second * 15,
		ReadTimeout:  time.Second * 15,
		IdleTimeout:  time.Second * 60,
		Handler:      neg, // Pass our instance of negroni  or gorilla/mux in.
	}

	// Run our server in a goroutine so that it doesn't block.
	go func() {
		if err := srv.ListenAndServe(); err != nil {
			log.Println(err)
		}
	}()

	c := make(chan os.Signal, 1)
	// We'll accept graceful shutdowns when quit via SIGINT (Ctrl+C)
	// SIGKILL, SIGQUIT or SIGTERM (Ctrl+/) will not be caught.
	signal.Notify(c, os.Interrupt)

	// Block until we receive our signal.
	<-c

	// Create a deadline to wait for.
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*15)
	defer cancel()
	// Doesn't block if no connections, but will otherwise wait until the timeout deadline.
	_ = srv.Shutdown(ctx)
	// Optionally, you could run srv.Shutdown in a goroutine and block on <-ctx.Done() if your application should wait
	// for other services to finalize based on context cancellation.
	log.Println("shutting down")
	os.Exit(0)
}
