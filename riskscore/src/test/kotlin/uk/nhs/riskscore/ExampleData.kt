package uk.nhs.riskscore

// This is the format of the simulation data from the Python GAEN repository.
internal val exampleDataSingleValuesRaw = listOf(
    Triple("2020-01-01T00:00:00", listOf(97.03021291), 201.06532745872457),
    Triple("2020-01-01T00:03:45.755652", listOf(85.81504891), 225.75565160910116),
    Triple("2020-01-01T00:08:25.133443", listOf(83.44136722), 279.3777909268962),
    Triple("2020-01-01T00:12:00.602426", listOf(64.5678367), 215.4689826255009),
    Triple("2020-01-01T00:19:54.724999", listOf(83.62187329), 211.07091989780974),
    Triple("2020-01-01T00:23:43.175339", listOf(73.51912742), 228.45034015853247),
    Triple("2020-01-01T00:26:46.664445", listOf(76.64553678), 183.48910595748023),
    Triple("2020-01-01T00:16:23.654079", listOf(78.41303166), 263.05165262029305),
    Triple("2020-01-01T00:29:56.663893", listOf(66.65088196), 189.9994479121982),
    Triple("2020-01-01T00:33:07.145239", listOf(70.27063972), 190.48134624246617)
)

internal val exampleDataSingleNonFinite = listOf(
    Triple("2020-01-01T00:00:00", listOf(97.03021291), 201.06532745872457),
    Triple("2020-01-01T00:03:45.755652", listOf(85.81504891), 225.75565160910116),
    Triple("2020-01-01T00:08:25.133443", listOf(83.44136722), 279.3777909268962),
    Triple("2020-01-01T00:12:00.602426", listOf(64.5678367), 215.4689826255009),
    Triple("2020-01-01T00:16:23.654079", listOf(78.41303166), 263.05165262029305),
    Triple("2020-01-01T00:19:54.724999", listOf(83.62187329), 211.07091989780974),
    Triple("2020-01-01T00:23:43.175339", listOf(73.51912742, Double.POSITIVE_INFINITY), 228.45034015853247),
    Triple("2020-01-01T00:26:46.664445", listOf(76.64553678), 183.48910595748023),
    Triple("2020-01-01T00:29:56.663893", listOf(66.65088196), 189.9994479121982),
    Triple("2020-01-01T00:33:07.145239", listOf(70.27063972), 190.48134624246617)
).map { ( _ , values, secondsSinceLastScan) ->
    ScanInstance(values.first(), secondsSinceLastScan)
}

internal val exampleDataSingleValues = exampleDataSingleValuesRaw.map { ( _ , values, secondsSinceLastScan) ->
    ScanInstance(values.first(), secondsSinceLastScan)
}

internal val exampleDataPublicApi = exampleDataSingleValuesRaw.map { ( _ , values, secondsSinceLastScan) ->
    ScanInstance(values.first().toInt(), secondsSinceLastScan.toInt())
}