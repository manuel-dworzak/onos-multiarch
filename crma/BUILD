COMPILE_DEPS = CORE_DEPS + JACKSON + [
    "@retrofit//jar",
    "@converter_moshi//jar",
    "@moshi//jar",
    "@okio//jar",
    "@okhttp//jar",
    "@logging_interceptor//jar",
    "@influxdb_java//jar",
]

INCLUDED_BUNDLES = [
    "@retrofit//jar",
    "@converter_moshi//jar",
    "@moshi//jar",
    "@okio//jar",
    "@okhttp//jar",
    "@logging_interceptor//jar",
    "@influxdb_java//jar",
    "//apps/crma:onos-apps-crma",
]

osgi_jar(
    deps = COMPILE_DEPS,
)

onos_app(
    app_name = "de.uniba.ktr.crma",
    category = "Monitoring",
    description = "A Cloudless Resource Monitoring App interworking with SDN",
    included_bundles = INCLUDED_BUNDLES,
    title = "Cloudless Resource Monitoring App",
    url = "https://www.uni-bamberg.de/en/ktr/",
)
