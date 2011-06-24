/*global describe, beforeEach, afterEach, it, expect, waitsFor, runs, Ns */

"use strict";

describe('Ext.Direct router', function () {
	var exceptionCount;
	var exceptionHandler = function () {
		exceptionCount += 1;
	};

	beforeEach(function () {
		exceptionCount = 0;
		Ext.direct.Manager.on('exception', exceptionHandler);
	});

	afterEach(function () {
		Ext.direct.Manager.un('exception', exceptionHandler);
	});

	it('should pass all primitive types as expected', function () {
		var callCompleted = false;
		Ns.Service.primitiveTypesEcho('a string', 123, true, 3.14, function (result) {
			expect(result).toEqual({
				stringValue: 'a string',
				intValue: 123,
				boolValue: true,
				doubleValue: 3.14
			});
			callCompleted = true;
		});

		waitsFor(function () {
			return callCompleted;
		}, 'Server call', 1000);
	});

	it("should not corrupt strings", function () {
		var callCompleted = false;
		Ns.Service.echo('тащий', function (result) {
			expect(result).toEqual('тащий');
			callCompleted = true;
		});

		waitsFor(function () {
			return callCompleted;
		}, 'Server call', 1000);
	});

	it("should batch calls", function () {
		var requestCount, responseCount = 0;
		var request = function (i) {
			Ns.Service.echo('call ' + i, function (result) {
				expect(result).toEqual('call ' + i);
				responseCount += 1;
			});
		};
		for (requestCount = 1; requestCount <= 10; requestCount += 1) {
			request(requestCount);
		}

		waitsFor(function () {
			return responseCount === 10;
		}, 'Server call', 1000);
	});

	it("should call methods without parameters", function () {
		var callCompleted = false;
		Ns.Service.noParams(function (result) {
			expect(result).toBeTruthy();
			callCompleted = true;
		});

		waitsFor(function () {
			return callCompleted;
		}, 'Server call', 1000);
	});

	it("should return exception", function () {
		var callCompleted = false;
		Ns.Service.exception(function (result, provider) {
			expect(result).toBeUndefined();
			callCompleted = true;
		});

		waitsFor(function () {
			return exceptionCount > 0;
		}, 'Server call', 1000);

		runs(function () {
			expect(exceptionCount).toEqual(1);
		});
	});
});
