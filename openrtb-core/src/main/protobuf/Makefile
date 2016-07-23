.PHONY: all

all: openrtb.pb.go

openrtb.pb.go:
	protoc -I/usr/local/include \
		-I. -I.. \
		-I${GOPATH}/src \
		-I${GOPATH}/src/github.com/gengo/grpc-gateway/third_party/googleapis \
		--go_out=plugins=grpc:. \
		--java_out=src/main/java \
		*.proto
